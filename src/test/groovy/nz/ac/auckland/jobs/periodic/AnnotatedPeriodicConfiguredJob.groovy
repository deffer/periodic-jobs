package nz.ac.auckland.jobs.periodic

import nz.ac.auckland.common.stereotypes.UniversityComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
@NamedJob("notificationConfiguredJob")
@DefaultConfiguration(delay = 3l, initialDelay = 3l)
@UniversityComponent
class AnnotatedPeriodicConfiguredJob extends Counter implements Job{

	private Logger log = LoggerFactory.getLogger(AnnotatedPeriodicConfiguredJob)

	@Override
	Runnable getRunnable() {
		return this.&execute
	}

	protected void execute(){
		super.execute()
		log.debug("AnnotatedPeriodicConfiguredJob run cycle $count")
	}

	/**
	 * This assumes that config was overridden in system property to 20:25
	 * @param vip
	 * @param delay
	 * @param initialDelay
	 * @return
	 */
	public boolean assertJobSchedule(boolean vip, long initialDelay, long delay){
		return !vip && delay == 25l && initialDelay == 20l
	}
}
