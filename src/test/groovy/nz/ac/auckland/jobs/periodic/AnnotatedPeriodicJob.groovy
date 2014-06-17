package nz.ac.auckland.jobs.periodic

import nz.ac.auckland.common.stereotypes.UniversityComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
@NamedJob("notificationJob")
@DefaultConfiguration(delay = 100l, initialDelay = 50l)
@UniversityComponent
class AnnotatedPeriodicJob extends Counter implements Job{

	private Logger log = LoggerFactory.getLogger(AnnotatedPeriodicJob)

	@Override
	Runnable getRunnable() {
		return this.&execute
	}

	protected void execute(){
		super.execute()
		log.debug("AnnotatedPeriodicJob run cycle $count")
	}

	public boolean assertJobSchedule(boolean vip, long initialDelay, long delay){
		return !vip && delay == 100l && initialDelay == 50l
	}
}
