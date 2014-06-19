package nz.ac.auckland.jobs.periodic

import java.util.concurrent.ScheduledFuture

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
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
public interface PeriodicJobs {
	public ScheduledJob getJobInfo(job)
	public List<ScheduledJob> listJobs()
	public void cancelJob(Job job)
	public ScheduledFuture<?> getFuture(Job job)
	public Map<Date, ScheduledJobEvent> getExecutionLog(Job job)
}