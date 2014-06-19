package nz.ac.auckland.jobs.periodic

import nz.ac.auckland.common.stereotypes.UniversityComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
@NamedJob("brokenNotificationJob")
@DefaultConfiguration(delay = 1L, initialDelay = 1L)
@UniversityComponent
class AnnotatedPeriodicBrokenJob extends Counter implements Job{

	private Logger log = LoggerFactory.getLogger(AnnotatedPeriodicBrokenJob)

	@Override
	Runnable getRunnable() {
		return this.&execute
	}
	static long waitTime = 1100

	protected void execute(){
		super.execute()
		Thread.sleep(waitTime)
		throw new NullPointerException('Ooops...')
	}


	public boolean assertJobSchedule(boolean vip, long initialDelay, long delay){
		return !vip && delay == 100l && initialDelay == 50l
	}
}
