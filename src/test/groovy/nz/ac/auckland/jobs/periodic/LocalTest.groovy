package nz.ac.auckland.jobs.periodic

import nz.ac.auckland.common.testrunner.GroupAppsSpringTestRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration

import javax.inject.Inject

/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
@ContextConfiguration("classpath:/applicationContext.xml")
@RunWith(GroupAppsSpringTestRunner)
class LocalTest {
	@Inject PeriodicJobs executor
	@Inject AnnotatedPeriodicVIPJob basicJob
	@Inject AnnotatedDisabledJob disabledJob
	@Inject AnnotatedPeriodicBrokenJob brokenJob

	@Before
	public void init() {

	}

	@Test
	public void testDeprecatedJobs(){
		int count = 0
		while (basicJob.count<3 && count < 7){
			Thread.sleep(500)
			count++
		}
		assert basicJob.count >= 2
		assert disabledJob.count==0

		// make sure disabled job is registered by not scheduled
		assert executor.getFuture(disabledJob) == null
		assert executor.getExecutionLog(disabledJob).isEmpty()

		// normal job should have few successful executions with non-empty finish time
		def logs = executor.getExecutionLog(basicJob).collect {key, value-> return value}.sort { return it.start }
		assert logs.size() >= 2

		for (ScheduledJobEvent event : logs){
			assert event.finish != null
			assert event.error == null
		}

		// broken job should have execution with logged error, but should continue running
		ScheduledJobEvent brokenExecution = executor.getExecutionLog(brokenJob).values().find {
			it.error != null
		}

		assert  brokenExecution != null
		assert brokenExecution.logMessage // just to make sure this methods is null-pointer safe
		assert (brokenExecution.finish.time - brokenExecution.start.time) >= AnnotatedPeriodicBrokenJob.waitTime
		assert !(executor.getFuture(brokenJob).isDone())
	}

	/**
	 * PeriodicJobs control panel package contains code below. Test that it wouldn't break
	 */
	/*@Test
	public void testBackwardCompatibility(){
		// wait for something to get executed couple times
		int count = 0
		while (basicJob.count<3 && count < 7){
			Thread.sleep(500)
			count++
		}
		assert basicJob.count >= 2

		ScheduledJobInfo jobInfo = executor.getJobInfo(basicJob)
		assert jobInfo.job.class.simpleName
		assert jobInfo.jobType
		assert jobInfo.future
		jobInfo.job.isEnabled()
		boolean failed = false
		jobInfo.executions.asMap().values().collect{
			failed = true
			assert it.start
			failed = false
		}
		assert !failed

		executor.listAllJobs().each {ScheduledJobInfo jobinfo ->
			failed = true
			assert jobInfo.job.class.simpleName
			assert jobInfo.jobType
			assert jobInfo.future
			failed = false
		}
		assert !failed
	} */

}
