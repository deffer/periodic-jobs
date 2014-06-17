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

		def disabledJob =  new AnnotatedDisabledJob()
		def initVIP = new AnnotatedInitVIPJob()
		def confJob = new AnnotatedPeriodicConfiguredJob()
		def periodicJob = new AnnotatedPeriodicJob()
		def vipJob = new AnnotatedPeriodicVIPJob()

		System.properties.put("jobs.notificationConfiguredJob.delay", "25")
		System.properties.put("jobs.notificationConfiguredJob.initialDelay", "20")

		pj.runnables = [disabledJob, initVIP, confJob, periodicJob, vipJob]

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
