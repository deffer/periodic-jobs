package nz.ac.auckland.jobs.periodic

import org.springframework.beans.factory.annotation.Autowired

import java.util.concurrent.ScheduledExecutorService

/**
 *
 * author: Irina Benediktovich - http://gplus.to/IrinaBenediktovich
 */
class DeprecatedPeriodicJobs {
	@Autowired(required = false)
	List<PeriodicJob> multiThreadRunnables

	@Autowired(required = false)
	List<QueuedPeriodicJob> singleThreadRunnables

	@Autowired(required = false)
	List<InitJob> initRunnables

	// execution log info. contains nz.ac.auckland.jobs.periodic.PeriodicJobs.ScheduledJobInfo
	Map<QueuedPeriodicJob, ScheduledJob> singleThreadJobs = [:]
	Map<PeriodicJob, ScheduledJob> multiThreadJobs = [:]
	Map<InitJob, ScheduledJob> initJobs = [:]


	protected void initDeprecatedJobs(Closure c, ScheduledExecutorService multiThreadExecutor, ScheduledExecutorService singleThreadExecutor){
		multiThreadRunnables.each { PeriodicJob job ->
			ScheduledJob jobInfo = c(job, multiThreadExecutor)
			multiThreadJobs.put(job, jobInfo)
		}

		singleThreadRunnables.each { QueuedPeriodicJob job ->
			ScheduledJob jobInfo = c(job, singleThreadExecutor)
			singleThreadJobs.put(job, jobInfo)
		}

		initRunnables.each { InitJob job ->
			ScheduledJob jobInfo = c(job, singleThreadExecutor)
			initJobs.put(job, jobInfo)
		}
	}

	protected String reportJobs(){
		if (singleThreadJobs.size()+multiThreadJobs.size()+initJobs.size() > 0)
			return "${multiThreadJobs.size()} normal jobs, ${singleThreadJobs.size()} queued jobs and ${initJobs.size()} init jobs found (deprecated)."
		else
			return ""
	}

}
