package nz.ac.auckland.jobs.periodic

import org.springframework.beans.factory.annotation.Autowired

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

}
