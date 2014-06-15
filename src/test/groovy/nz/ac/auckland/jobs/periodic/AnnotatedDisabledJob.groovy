package nz.ac.auckland.jobs.periodic

import nz.ac.auckland.common.stereotypes.UniversityComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
@DefaultConfiguration(enabled = false)
@UniversityComponent
class AnnotatedDisabledJob extends Counter implements Job{

	private Logger log = LoggerFactory.getLogger(AnnotatedDisabledJob)

	protected void execute(){
		super.execute()
		log.debug("Finished AnnotatedDisabledJob, cycle $count")
	}

	@Override
	Runnable getRunnable() {
		return this.&execute;
	}
}
