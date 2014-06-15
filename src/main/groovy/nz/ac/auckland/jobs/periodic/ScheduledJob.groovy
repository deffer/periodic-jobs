package nz.ac.auckland.jobs.periodic

import com.google.common.cache.Cache
import nz.ac.auckland.jobs.periodic.depr.WrapperFactory

import java.util.concurrent.ScheduledFuture

/**
 * All information about certain job as collected at runtime (configuration read from properties,
 *   types of the job derived from configuration, scheduled future)
 */
class ScheduledJob {
	AbstractJob job
	AbstractJob wrapper

	ScheduledFuture<?> future;
	protected Cache<Date, ScheduledJobEvent> executions;

	Job instance
	boolean privileged
	boolean isPeriodic
	long delay
	long initialDelay
	boolean enabled
	String cron


	AbstractJob getJob(){
		if (job)
			return job
		else{
			if (!wrapper)
				wrapper = WrapperFactory.wrapJob(this)
			return wrapper
		}
	}

	boolean isPeriodic(){
		if (this.job)
			return job instanceof AbstractPeriodicJob
		else{
			return isPeriodic
		}
	}

	Long getInitialDelay(){
		if (this.job) {
			Long result = job.initialDelay
			return result?: defaultInitialDelay
		} else {
			return initialDelay
		}
	}

	Long getPeriodicDelay(){
		if (isPeriodic()) {
			if (this.job) {
				Long result = ((AbstractPeriodicJob) job).periodicDelay
				return result?: defaultPeriodicDelay
			} else {
				return delay
			}
		} else {
			return null
		}
	}

	String getJobType(){
		if (job){
			if (job instanceof PeriodicJob)
				return 'Periodic'
			else if (job instanceof QueuedPeriodicJob)
				return 'Queued'
			else if (job instanceof InitJob)
				return 'Init'
			else
				return 'Unknown'
		}else{
			String result = "Init"
			if (!periodic){
				result = "Periodic" + cron?" (cron)" : ""
			}
			if (privileged)
				result+= "(privileged)"
			return result
		}
	}
}
