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
class AnnotatedDisabledJob implements Job{

	private Logger log = LoggerFactory.getLogger(AnnotatedDisabledJob)

	int count = 0

	protected void execute(){
		count ++
		log.debug("Finished AnnotatedDisabledJob, cycle $count")
	}

	@Override
	Runnable getRunnable() {
		return this.&execute;
	}
}
