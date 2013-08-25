package nz.ac.auckland.jobs.periodic

import net.stickycode.stereotype.configured.PostConfigured
import nz.ac.auckland.common.config.ConfigKey
import nz.ac.auckland.common.stereotypes.UniversityComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

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
 */
@CompileStatic
@UniversityComponent
class PeriodicJobs {

	private Logger log = LoggerFactory.getLogger(PeriodicJobs.class)

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

	ScheduledExecutorService multiThreadExecutor = Executors.newScheduledThreadPool(3)
	ScheduledExecutorService singleThreadExecutor = Executors.newSingleThreadScheduledExecutor()

	Map<QueuedPeriodicJob, ScheduledFuture<?>> singleThreadJobs = [:]
	Map<PeriodicJob, ScheduledFuture<?>> multiThreadJobs = [:]
	Map<InitJob, ScheduledFuture<?>> initJobs = [:]

	@PostConfigured
	public void init(){
		if (!enabled){
			log.warn("Periodic jobs are disabled. To enable remove periodicJobs.enabled or set to true")
			return
		}
		multiThreadRunnables.each { PeriodicJob job ->
			if (job.isEnabled()){
				Long initialDelay = job.initialDelay != null? job.initialDelay : defaultInitialDelay
				Long periodicDelay = job.periodicDelay != null? job.periodicDelay : defaultPeriodicDelay
				ScheduledFuture<?> future = multiThreadExecutor.scheduleWithFixedDelay(job.runnable, initialDelay, periodicDelay, TimeUnit.SECONDS)
				multiThreadJobs.put(job, future)
			}
		}

		singleThreadRunnables.each { QueuedPeriodicJob job ->
			if (job.isEnabled()){
				Long initialDelay = job.initialDelay != null? job.initialDelay : defaultInitialDelay
				Long periodicDelay = job.periodicDelay != null? job.periodicDelay : defaultPeriodicDelay
				ScheduledFuture<?> future = singleThreadExecutor.scheduleWithFixedDelay(job.runnable, initialDelay, periodicDelay, TimeUnit.SECONDS)
				singleThreadJobs.put(job, future)
			}
		}

		initRunnables.each { InitJob job ->
			if (job.isEnabled()){
				Long initialDelay = job.initialDelay != null? job.initialDelay : defaultInitialDelay
				ScheduledFuture<?> future = singleThreadExecutor.schedule(job.runnable, initialDelay, TimeUnit.SECONDS)
				initJobs.put(job, future)
			}
		}

		log.info("${multiThreadJobs.size()} normal jobs, ${singleThreadJobs.size()} queued jobs and ${initJobs.size()} init jobs registered")
	}

	/**
	 * Cancels given gob. If job is running, will wait for job to finish.
	 * If there is a need to cancel job even if its still running, use getFuture() method.
	 * @param job job to cancel. Has to be one of supported job interfaces.
	 */
	public void cancelJob(Object job){
		getFuture(job)?.cancel(false)
	}

	/**
	 * Returns ScheduledFuture attached to given job. Contains some not very useful info.
	 * ScheduledFuture.isDone() returns true if job has finished and is not scheduled to run again (for instance after periodic task was cancelled).
	 * @param job
	 * @return
	 */
	public ScheduledFuture<?> getFuture(Object job){
		return multiThreadJobs.get(job)?: (singleThreadJobs.get(job)?: initJobs.get(job))
	}
}
