package nz.ac.auckland.jobs.periodic

import nz.ac.auckland.common.stereotypes.UniversityComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
@NamedJob("disabledInConfigJob")
@DefaultConfiguration(delay = 4l, initialDelay = 4l)
@UniversityComponent
class AnnotatedDisabledInConfigJob extends Counter implements Job{

	private Logger log = LoggerFactory.getLogger(AnnotatedDisabledInConfigJob)

	@Override
	Runnable getRunnable() {
		return this.&execute
	}

	protected void execute(){
		super.execute()
		log.debug("AnnotatedDisabledInConfigJob run cycle $count")
	}

	/**
	 * This assumes that job is disabled in config
	 * @param vip
	 * @param delay
	 * @param initialDelay
	 * @return
	 */
	public boolean assertJobSchedule(boolean vip, long initialDelay, long delay){
		return false
	}
}
