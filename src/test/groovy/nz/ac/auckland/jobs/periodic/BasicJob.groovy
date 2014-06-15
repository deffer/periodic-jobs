package nz.ac.auckland.jobs.periodic

import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.jobs.periodic.PeriodicJob
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 * @deprecated
 */
@UniversityComponent
class BasicJob extends Counter implements PeriodicJob{

	private Logger log = LoggerFactory.getLogger(BasicJob)

	protected void execute(){
		super.execute()
		log.debug("Basic job run cycle $count")
	}

	@Override
	Runnable getRunnable() {
		return this.&execute;
	}

	@Override
	Long getInitialDelay() {
		return 1
	}

	@Override
	Long getPeriodicDelay() {
		return 1
	}

	@Override
	Boolean isEnabled() {
		return true
	}
}
