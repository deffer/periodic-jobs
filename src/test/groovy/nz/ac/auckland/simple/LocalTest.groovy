package nz.ac.auckland.simple

import net.stickycode.bootstrap.StickyBootstrap
import nz.ac.auckland.common.testrunner.GroupAppsSpringTestRunner
import nz.ac.auckland.common.testrunner.GroupAppsUnitTestRunner
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
	@Inject StickyBootstrap bootstrap

	@Test
	public void testZeJobsTest(){
		int count = 0
		ScheduledFuture<?> future = executor.getFuture(basicJob)
		while (basicJob.count<2 && count < 5){
			Thread.sleep(500)
			count++
		}
		assert  basicJob.count >= 2
	}

	@Before
	public void init() {
		bootstrap.start()
	}
}
