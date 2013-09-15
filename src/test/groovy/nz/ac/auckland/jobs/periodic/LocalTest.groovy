package nz.ac.auckland.jobs.periodic

import net.stickycode.bootstrap.StickyBootstrap
import nz.ac.auckland.common.testrunner.GroupAppsSpringTestRunner
import nz.ac.auckland.common.testrunner.GroupAppsUnitTestRunner
import nz.ac.auckland.jobs.periodic.PeriodicJobs
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration

import javax.inject.Inject
import java.util.concurrent.ScheduledFuture


@ContextConfiguration("classpath:/applicationContext.xml")
@RunWith(GroupAppsSpringTestRunner)
class LocalTest {
	@Inject PeriodicJobs executor
	@Inject BasicJob basicJob
	@Inject DisabledJob disabledJob
	@Inject BrokenJob brokenJob

	@Before
	public void init() {

	}

	@Test
	public void testJobs(){
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

		for (PeriodicJobs.ExecutionEvent event : logs){
			assert event.finish != null
			assert event.error == null
		}

		// broken job should have execution with logged error, but should continue running
		PeriodicJobs.ExecutionEvent brokenExecution = executor.getExecutionLog(brokenJob).values().find {
			it.error != null
		}

		assert  brokenExecution != null
		assert brokenExecution.logMessage // just to make sure this methods is null-pointer safe
		assert (brokenExecution.finish.time - brokenExecution.start.time) >= BrokenJob.waitTime
		assert !(executor.getFuture(brokenJob).isDone())
	}

}
