package nz.ac.auckland.jobs.periodic

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import groovy.transform.TypeCheckingMode
import net.stickycode.stereotype.configured.PostConfigured
import nz.ac.auckland.common.config.ConfigKey
import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.jobs.periodic.depr.WrapperFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

import java.lang.annotation.Annotation
import java.text.SimpleDateFormat
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

import groovy.transform.CompileStatic

/**
 *  Collects jobs (classes extending Job and optionally annotated with DefaultConfiguration, NamedJob, Privileged)
 *     and schedules them for execution according to their configuration.
 *
 *  Has two execution pools: single thread and multithread.
 *
 *  Privileged jobs use single thread, which means none of the privileged jobs can run simultaneously
 *    with another privileged job.  If job A has to start while other job is still running, job A will have to wait.
 *
 *  Not privileged jobs use multithread pool. It will grow to as many treads as required to run all jobs
 *    with respect to their initial and periodic delay setting.
 *
 * Job with negative delay will only be invoked once, respecting privileged and initialDelay configuration.
 *
 * Configuration:
 *
 * Configuration is read from @DefaultConfiguration but can be overridden in the System properties. To enable
 *   this override, job needs to be annotated with NamedJob and this name will be used to look up
 *   properties.
 *
 * Job has to be enabled (in configuration) otherwise it will not be scheduled (this is usable for instance
 *    when some jobs has to be turned off during tests)
 *
 *  Scheduler can be turned off globally (for all jobs) by setting System property periodicJobs.enabled=false
 *
 *  Deprecated functionality:
 *
 *  This is a transition from old-style job definition (PeriodicQueuedJob, PeriodicJob, InitJob) to new style
 *  (extends Job and annotated with @DefaultConfiguration). At the moment it supports both, but old style will be
 *  dropped in the next release.
 *
 * author: Irina Benediktovich - http://gplus.to/IrinaBenediktovich
 */
@CompileStatic
@UniversityComponent
class PeriodicJobs extends DeprecatedPeriodicJobs {

	private Logger log = LoggerFactory.getLogger(PeriodicJobs.class)

	String logInstance

	static int LOG_CACHE_SIZE = 100
	static SimpleDateFormat df = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')

	@Autowired(required = false)
	List<Job> runnables

	@ConfigKey("periodicJobs.enabled")
	Boolean enabled = true

	@ConfigKey("periodicJobs.defaultInitialDelay")
	Long defaultInitialDelay = 5

	@ConfigKey("periodicJobs.defaultInitialDelay")
	Long defaultPeriodicDelay = 300

	// executors
	ScheduledExecutorService multiThreadExecutor = Executors.newScheduledThreadPool(3)
	ScheduledExecutorService singleThreadExecutor = Executors.newSingleThreadScheduledExecutor()

	Map<Job, ScheduledJobInfo> jobs = [:]

	boolean initialized = false // spring calls init() several times, so we need to make sure we only initialize once

	@CompileStatic(TypeCheckingMode.SKIP)
	@PostConfigured
	public void init(){
		if (!enabled){
			log.warn('Periodic jobs are disabled. To enable remove periodicJobs.enabled or set to true')
			return
		}

		boolean needToRun = false
		synchronized (this){
			if (!initialized){
				needToRun = true
				initialized = true
			}
		}

		if (!needToRun){
			log.warn('Already initialized')
			return
		}

		initDeprecatedJobs(this.&createDeprecatedJob, multiThreadExecutor, singleThreadExecutor)

		List<Job> deprecatedJobs = [] + multiThreadRunnables + singleThreadRunnables + initRunnables
		runnables.each {Job job ->
			if (!(job in deprecatedJobs)){
				ScheduledJobInfo jobInfo = createJob(job)
				jobs.put(job, jobInfo)
			}
		}

		logInstance  = this.toString()
		if (logInstance.indexOf('@')>0){
			logInstance = logInstance.split('@').last()
		}

		String message = reportJobs()
		if (jobs) message += " ${jobs.size()} jobs found (new)."

		log.info(message?:"No jobs found." + " Instance: $logInstance")
	}

	/**
	 * Only returns deprecated jobs
	 * @return
	 * @deprecated use listJobs to see all jobs
	 */
	@CompileStatic(TypeCheckingMode.SKIP)
	public List<ScheduledJobInfo> listAllJobs(){
		return [] + multiThreadJobs.values() + singleThreadJobs.values() + initJobs.values()
	}

	public List<ScheduledJob> listJobs(){
		def result = listAllJobs()
		result += jobs.values()
		return result
	}

	/**
	 * Cancels given gob. If job is running, will wait for job to finish.
	 * If there is a need to cancel job even if its still running, use getFuture() method.
	 *
	 * Cancelled job cannot be restarted.
	 *
	 * @param job job to cancel. Has to be one of supported job interfaces.
	 */
	public void cancelJob(AbstractJob job){
		getFuture(job)?.cancel(false)
	}

	/**
	 * Returns ScheduledFuture attached to given job. Contains some not very useful info.
	 * ScheduledFuture.isDone() returns true if job has finished and is not scheduled to run again (for instance after periodic task was cancelled).
	 * @param job
	 * @return
	 * @deprecated
	 */
	public ScheduledFuture<?> getFuture(AbstractJob job){
		return getJobInfo(job)?.future
	}

	/**
	 * Returns ScheduledFuture attached to given job. Contains some not very useful info.
	 * ScheduledFuture.isDone() returns true if job has finished and is not scheduled to run again (for instance after periodic task was cancelled).
	 * @param job
	 * @return
	 */
	public ScheduledFuture<?> getFuture(Job job){
		return getJobInfo(job)?.future
	}

	public Map<Date, ScheduledJobEvent> getExecutionLog(Job job){
		return getJobInfo(job)?.executions?.asMap()
	}

	/**
	 *
	 * @param job
	 * @return
	 * @deprecated
	 */
	@CompileStatic(TypeCheckingMode.SKIP)
	public Map<Date, ExecutionEvent> getExecutionLog(AbstractJob job){
		Map<Date, ExecutionEvent> result = [:]
		getJobInfo(job)?.executions?.asMap()?.each{Date k, ScheduledJobEvent v ->
			if (v instanceof ExecutionEvent)
				result.put(k, v as ExecutionEvent)
		}
		return result
	}

	/**
	 *
	 * @param job
	 * @return
	 * @deprecated
	 */
	@CompileStatic(TypeCheckingMode.SKIP)
	protected ScheduledJobInfo getJobInfo(AbstractJob job){
		ScheduledJob result = multiThreadJobs.get(job)?: (singleThreadJobs.get(job)?: initJobs.get(job))
		return result as ScheduledJobInfo
	}

	protected ScheduledJob getJobInfo(job){
		return jobs.get(job)
	}

	/**
	 *
	 * @param job
	 * @param executor
	 * @return
	 * @deprecated
	 */
	@CompileStatic(TypeCheckingMode.SKIP)
	protected ScheduledJob createDeprecatedJob(AbstractJob job, ScheduledExecutorService executor){
		ScheduledJobInfo jobInfo = new ScheduledJobInfo(job:job)
		Cache<Date, ScheduledJobEvent> cache = CacheBuilder.newBuilder().maximumSize(LOG_CACHE_SIZE).build()
		jobInfo.executions = cache

		if (!job.isEnabled()){
			log.debug("Deprecated job ${job} will not be schedules because it is disabled.")
		}else{
			scheduleJob(job, executor, jobInfo)
		}

		return jobInfo
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	protected ScheduledJobInfo createJob(Job job){
		ScheduledJobInfo jobInfo = new ScheduledJobInfo(instance: job)
		initJob(job, jobInfo)

		if (!jobInfo.enabled){
			log.debug("Job ${job} will not be schedules because it is disabled.")
		}else{
			Cache<Date, ScheduledJobEvent> cache = CacheBuilder.newBuilder().maximumSize(LOG_CACHE_SIZE).build()
			jobInfo.executions = cache  // cant do this assignment with CompileStatic on (o_0)

			scheduleJob(job, jobInfo.isPrivileged()?singleThreadExecutor:multiThreadExecutor, jobInfo)
		}

		return jobInfo
	}

	protected void initJob(Job job, ScheduledJob into){
		if (job.class.getAnnotation(Privileged)){
			into.privileged = true
		}


		Boolean enabled
		NamedJob named = job.class.getAnnotation(NamedJob)
		if (named){  // read from properties
			into.name = named.value()
			into.displayName = named.displayName()
			if (into.name && named.propertyConfigurable()){
				into.delay = readLong(into.name, "delay")
				into.initialDelay = readLong(into.name, "initialDelay")
				into.cron = System.getProperty("jobs.${into.name}.cron")
				enabled = !System.getProperty("jobs.${into.name}.enabled")?.equalsIgnoreCase("false")
			}
		}

		DefaultConfiguration defaultConfig = job.class.getAnnotation(DefaultConfiguration)
		if (defaultConfig){
			if (!into.initialDelay) into.initialDelay = defaultConfig.initialDelay()
			if (!into.delay) into.delay = defaultConfig.delay()
			if (!into.cron) into.cron = defaultConfig.cron()
			if (enabled == null) enabled = defaultConfig.enabled()
		}else{
			if (!into.cron){
				if (!into.initialDelay) into.initialDelay = 5l
				if (!into.delay) into.delay = 300l
			}
			if (enabled == null) enabled = true
		}
		into.enabled = enabled
	}

	protected Long readLong(String jobName, String property){
		String propDelay = System.getProperty("jobs.$jobName.$property")
		if (propDelay && propDelay.isLong())
			return propDelay.toLong()
		else
			return null
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	protected void scheduleJob(Job job, ScheduledExecutorService executor, ScheduledJobInfo into){
		ScheduledJobInfo jobInfo = into
		Closure wrapper = {
			log.debug("Job running: ${job.class.name}")
			ExecutionEvent event = new ExecutionEvent(start: new Date())
			jobInfo.addExecution(event.start, event)

			// TODO if its cron and initial delay has not passed yet, skip execution
			try{
				job.runnable.run()
			}catch (Throwable error){
				event.error = error
			}
			event.finish = new Date()
			return jobInfo
		}

		log.debug("Scheduling job: ${job.class.name} (${into.getInitialDelay()}:${into.getPeriodicDelay()})")

		if (jobInfo.cron){
			 // TODO schedule cron job
		}else{
			if (jobInfo.isPeriodic()){
				jobInfo.future = executor.scheduleWithFixedDelay(wrapper, jobInfo.getInitialDelay(), jobInfo.getPeriodicDelay(), TimeUnit.SECONDS)
			}else{
				jobInfo.future = executor.schedule(wrapper, jobInfo.getInitialDelay(), TimeUnit.SECONDS)
			}
		}
	}

	/**
	 * @deprecated
	 */
	class ScheduledJobInfo extends ScheduledJob{
		Long getInitialDelay(){
			if (this.job) {
				Long result = job.initialDelay
				return result?: defaultInitialDelay
			} else {
				return this.@initialDelay
			}
		}

		Long getPeriodicDelay(){
			if (isPeriodic()) {
				if (this.job) {
					Long result = ((AbstractPeriodicJob) job).periodicDelay
					return result?: defaultPeriodicDelay
				} else {
					return this.@delay
				}
			} else {
				return null
			}
		}

		protected void addExecution(Date date, ExecutionEvent event){
			try{
				this.@executions.put(date, event)
			}catch (Exception e){
				e.printStackTrace()
			}
		}
	}

	/**
	 * @deprecated
	 */
	class ExecutionEvent extends ScheduledJobEvent{

	}
}
