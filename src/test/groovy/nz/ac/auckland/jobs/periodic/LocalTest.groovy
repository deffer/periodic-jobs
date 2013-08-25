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
	@Inject StickyBootstrap bootstrap

	@Test
	public void testJobs(){
		int count = 0
		while (basicJob.count<3 && count < 7){
			Thread.sleep(500)
			count++
		}
		assert basicJob.count >= 2
		assert disabledJob.count==0
	}


	@Before
	public void init() {
		bootstrap.start()
	}
}
