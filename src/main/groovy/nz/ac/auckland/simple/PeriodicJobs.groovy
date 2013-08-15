package nz.ac.auckland.simple

import net.stickycode.stereotype.configured.PostConfigured
import nz.ac.auckland.common.config.ConfigKey
import nz.ac.auckland.common.stereotypes.UniversityComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 *  ScheduledFuture.isDone() returns true after periodic task was cancelled
 */
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
	boolean enabled = true

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
			Long initialDelay = job.initialDelay != null? job.initialDelay : defaultInitialDelay
			Long periodicDelay = job.periodicDelay != null? job.periodicDelay : defaultPeriodicDelay
			ScheduledFuture<?> future = multiThreadExecutor.scheduleWithFixedDelay(job.runnable, initialDelay, periodicDelay, TimeUnit.SECONDS)
			multiThreadJobs.put(job, future)
		}

		singleThreadRunnables.each { QueuedPeriodicJob job ->
			Long initialDelay = job.initialDelay != null? job.initialDelay : defaultInitialDelay
			Long periodicDelay = job.periodicDelay != null? job.periodicDelay : defaultPeriodicDelay
			ScheduledFuture<?> future = singleThreadExecutor.scheduleWithFixedDelay(job.runnable, initialDelay, periodicDelay, TimeUnit.SECONDS)
			singleThreadJobs.put(job, future)
		}

		initRunnables.each { InitJob job ->
			Long initialDelay = job.initialDelay != null? job.initialDelay : defaultInitialDelay
			ScheduledFuture<?> future = singleThreadExecutor.schedule(job.runnable, initialDelay, TimeUnit.SECONDS)
			initJobs.put(job, future)
		}

		log.info("${multiThreadJobs.size()} normal jobs, ${singleThreadJobs.size()} queued jobs and ${initJobs.size()} init jobs registered")
	}

	public ScheduledFuture<?> getFuture(Object job){
		return multiThreadJobs.get(job)?: (singleThreadJobs.get(job)?: initJobs.get(job))
	}
}
