package nz.ac.auckland.jobs.periodic

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import groovy.transform.TypeCheckingMode
import net.stickycode.stereotype.configured.PostConfigured
import nz.ac.auckland.common.config.ConfigKey
import nz.ac.auckland.common.stereotypes.UniversityComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

import java.text.SimpleDateFormat
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

import groovy.transform.CompileStatic

/**
 *  Collects jobs (classes annotated with InitJob,PeriodicJob,QueuedPeriodicJob) and schedules them
 *    for execution.
 *
 *  Has two execution pools: single thread and multithread.
 *
 *  InitJobs are only executed once and use single thread.
 *  QueuePeriodicJobs use single thread, which means none of the Queued jobs can run simultaneously
 *    with another Queued or Init job.  If job A has to start while other job is still running, job A will have to wait.
 *  PeriodicJobs use multithread pool. It will grow to as many treads as required to run all PeriodicJobs
 *    with respect to their initial and periodic delay setting.
 *
 *  Job has to be enabled (isEnabled()==true) otherwise it will not be scheduled (this is usable for instance
 *    when some jobs has to be turned off during tests)
 *
 *  Scheduler can be turned off globally (for all jobs) by setting System property periodicJobs.enabled=false
 *
 * author: Irina Benediktovich - http://gplus.to/IrinaBenediktovich
 */
@CompileStatic
@UniversityComponent
class PeriodicJobs {

	private Logger log = LoggerFactory.getLogger(PeriodicJobs.class)

	String logInstance

	static int LOG_CACHE_SIZE = 100
	static SimpleDateFormat df = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')

	@Autowired(required = false)
	List<PeriodicJob> multiThreadRunnables

	@Autowired(required = false)
	List<QueuedPeriodicJob> singleThreadRunnables

	@Autowired(required = false)
	List<InitJob> initRunnables

	@ConfigKey("periodicJobs.enabled")
	Boolean enabled = true

	@ConfigKey("periodicJobs.defaultInitialDelay")
	Long defaultInitialDelay = 5

	@ConfigKey("periodicJobs.defaultInitialDelay")
	Long defaultPeriodicDelay = 300

	// executors
	ScheduledExecutorService multiThreadExecutor = Executors.newScheduledThreadPool(3)
	ScheduledExecutorService singleThreadExecutor = Executors.newSingleThreadScheduledExecutor()

	// execution log info
	Map<QueuedPeriodicJob, ScheduledJobInfo> singleThreadJobs = [:]
	Map<PeriodicJob, ScheduledJobInfo> multiThreadJobs = [:]
	Map<InitJob, ScheduledJobInfo> initJobs = [:]

	boolean initialized = false

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

		multiThreadRunnables.each { PeriodicJob job ->
			ScheduledJobInfo jobInfo = createJob(job, multiThreadExecutor)
			multiThreadJobs.put(job, jobInfo)
		}

		singleThreadRunnables.each { QueuedPeriodicJob job ->
			ScheduledJobInfo jobInfo = createJob(job, singleThreadExecutor)
			singleThreadJobs.put(job, jobInfo)
		}

		initRunnables.each { InitJob job ->
			ScheduledJobInfo jobInfo = createJob(job, singleThreadExecutor)
			initJobs.put(job, jobInfo)
		}

		logInstance  = this.toString()
		if (logInstance.indexOf('@')>0){
			logInstance = logInstance.split('@').last()
		}

		log.info("${multiThreadJobs.size()} normal jobs, ${singleThreadJobs.size()} queued jobs and ${initJobs.size()} init jobs registered. Instance: $logInstance")
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
	 */
	public ScheduledFuture<?> getFuture(AbstractJob job){
		return getJobInfo(job)?.future
	}

	public Map<Date, ExecutionEvent> getExecutionLog(AbstractJob job){
		return getJobInfo(job)?.executions?.asMap()
	}

	protected ScheduledJobInfo getJobInfo(AbstractJob job){
		return multiThreadJobs.get(job)?: (singleThreadJobs.get(job)?: initJobs.get(job))
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	protected ScheduledJobInfo createJob(AbstractJob job, ScheduledExecutorService executor){
		ScheduledJobInfo jobInfo = new ScheduledJobInfo(job:job)
		Cache<Date, ExecutionEvent> cache = CacheBuilder.newBuilder().maximumSize(LOG_CACHE_SIZE).build()
		jobInfo.executions = cache  // cant do this assignment with CompileStatic on (o_0)

		if (!job.isEnabled()){
			log.debug("Job ${job} will not be schedules because it is disabled.")
		}else{
			scheduleJob(job, executor, jobInfo)
		}

		return jobInfo
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	protected void scheduleJob(AbstractJob job, ScheduledExecutorService executor, ScheduledJobInfo into){
		ScheduledJobInfo jobInfo = into
		Closure wrapper = {
			ExecutionEvent event = new ExecutionEvent(start: new Date())
			jobInfo.executions.put(event.start, event)
			// TODO if disabled - skip execution
			try{
				job.runnable.run()
			}catch (Throwable error){
				event.error = error
			}
			event.finish = new Date()
		}

		Long initialDelay = job.initialDelay != null? job.initialDelay : defaultInitialDelay
		if (job instanceof AbstractPeriodicJob){
			Long periodicDelay = ((AbstractPeriodicJob)job).periodicDelay != null? ((AbstractPeriodicJob)job).periodicDelay : defaultPeriodicDelay
			jobInfo.future = executor.scheduleWithFixedDelay(wrapper, initialDelay, periodicDelay, TimeUnit.SECONDS)
		}else{
			jobInfo.future = executor.schedule(wrapper, initialDelay, TimeUnit.SECONDS)
		}
	}

	class ScheduledJobInfo{
		AbstractJob job
		ScheduledFuture<?> future;
		Cache<Date, ExecutionEvent> executions;

		boolean isPeriodic(){
			return job instanceof AbstractPeriodicJob
		}

		Long getPeriodicDelay(){
			if (isPeriodic()){
				Long delay = ((AbstractPeriodicJob)job).periodicDelay
				return delay ?: defaultPeriodicDelay
			}else{
				return null
			}
		}
	}

	class ExecutionEvent {
		Date start
		Date finish
		Throwable error
		String getLogMessage(){
			if (error){
				return "${df.format(start)} - job has resulted in ${error.class.simpleName}: ${error.getMessage()} at ${df.format(finish)}"
			}else if (finish == null){
				return "${df.format(start)} - job is still running..."
			}else{
				return "${df.format(start)} - ${df.format(finish)}"
			}
		}
	}
}
