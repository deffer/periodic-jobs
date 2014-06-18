package nz.ac.auckland.jobs.periodic.depr

import nz.ac.auckland.jobs.periodic.AbstractJob
import nz.ac.auckland.jobs.periodic.Job
import nz.ac.auckland.jobs.periodic.PeriodicJobs
import nz.ac.auckland.jobs.periodic.ScheduledJob

/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
class WrapperFactory {

	public static AbstractJob wrapJob(ScheduledJob job){
		return new AbstractJob() {
			@Override
			Long getInitialDelay() {
				return 1
			}

			@Override
			Boolean isEnabled() {
				return job?.isEnabled()
			}

			@Override
			Runnable getRunnable() {
				return job?.getInstance()?.runnable
			}
		}
	}
}
