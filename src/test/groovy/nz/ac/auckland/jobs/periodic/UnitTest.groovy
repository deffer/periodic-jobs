package nz.ac.auckland.jobs.periodic

import org.junit.Test

import java.util.concurrent.ScheduledExecutorService

/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
class UnitTest {
	@Test
	public void testAnnotatedJobSchedule(){
		PeriodicJobs pj = new PeriodicJobs()

		System.properties.put("jobs.notificationConfiguredJob.delay", "25")
		System.properties.put("jobs.notificationConfiguredJob.initialDelay", "20")
		System.properties.put("jobs.disabledInConfigJob.enabled", "false")

		pj.runnables = [new AnnotatedDisabledJob(), new AnnotatedInitVIPJob(), new AnnotatedPeriodicConfiguredJob(),
				new AnnotatedPeriodicJob(), new AnnotatedPeriodicVIPJob(), new AnnotatedDisabledInConfigJob()]

		def errors = []
		def scheduled = []
		pj.singleThreadExecutor = [
				scheduleWithFixedDelay : {wrapper, initialDelay, periodicDelay, ignore->
					def jobInfo = wrapper.call()
					if (!jobInfo.instance.assertJobSchedule(true, initialDelay, periodicDelay)){
						errors << "Job is incorrectly shceduled ${jobInfo.instance.class.name} ($initialDelay:$periodicDelay vip)"
					} else{
						scheduled << jobInfo.instance
					}
					return null
				},
				schedule: {wrapper, initialDelay, ignore->
					def jobInfo = wrapper.call()
					if (!jobInfo.instance.assertJobSchedule(true, initialDelay, -1)){
						errors << "Job is incorrectly shceduled ${jobInfo.instance.class.name} ($initialDelay:-1 vip)"
					}  else{
						scheduled << jobInfo.instance
					}
					return null
				}
		] as ScheduledExecutorService

		pj.multiThreadExecutor = [
				scheduleWithFixedDelay : {wrapper, initialDelay, periodicDelay, ignore->
					def jobInfo = wrapper.call()
					if (!jobInfo.instance.assertJobSchedule(false, initialDelay, periodicDelay)){
						errors << "Job is incorrectly shceduled ${jobInfo.instance.class.name} ($initialDelay:$periodicDelay)"
					} else{
						scheduled << jobInfo.instance
					}
					return null
				},
				schedule: {wrapper, initialDelay, ignore->
					def jobInfo = wrapper.call()
					if (!jobInfo.instance.assertJobSchedule(false, initialDelay, -1)){
						errors << "Job is incorrectly shceduled ${jobInfo.instance.class.name} ($initialDelay:-1)"
					} else{
						scheduled << jobInfo.instance
					}
					return null
				}
		] as ScheduledExecutorService

		pj.init()

		for (int i in errors){
			assert errors[i] == ""
		}

		assert scheduled.size() == 4

	}
}
