package nz.ac.auckland.jobs.periodic

import com.google.common.cache.Cache

import java.util.concurrent.ScheduledFuture

/**
 * All information about certain job as collected at runtime (configuration read from properties,
 *   types of the job derived from configuration, scheduled future)
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
class ScheduledJob {

	protected ScheduledFuture<?> future;
	protected Cache<Date, ScheduledJobEvent> executions;

	protected Job instance
	String name
	String displayName
	boolean privileged = false
	boolean isPeriodic = true
	protected Long delay
	protected Long initialDelay
	boolean enabled = true
	String cron

	boolean isPeriodic(){
		return isPeriodic
	}

	public String getJobType(){
		String result = "Init"
		if (!periodic){
			result = "Periodic" + cron?" (cron)" : ""
		}
		if (privileged)
			result+= "(privileged)"
		return result
	}
}
