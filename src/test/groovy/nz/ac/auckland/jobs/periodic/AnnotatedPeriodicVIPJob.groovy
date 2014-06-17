package nz.ac.auckland.jobs.periodic

import nz.ac.auckland.common.stereotypes.UniversityComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
@Privileged
@NamedJob("reindexingJob") // lets you configure job in property file
@DefaultConfiguration(delay = 1l, initialDelay = 1l) // if there is no property file (during development for instance)
@UniversityComponent
class AnnotatedPeriodicVIPJob extends Counter implements Job{

	private Logger log = LoggerFactory.getLogger(AnnotatedPeriodicVIPJob)

	@Override
	Runnable getRunnable() {
		return this.&execute
	}

	protected void execute(){
		super.execute()
		log.debug("AnnotatedPeriodicVIPJob run cycle $count")
	}

	public boolean assertJobSchedule(boolean vip, long initialDelay, long delay){
		return vip && delay == 1l && initialDelay == 1l
	}
}
