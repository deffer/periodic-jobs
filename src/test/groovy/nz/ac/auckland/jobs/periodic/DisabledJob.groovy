package nz.ac.auckland.jobs.periodic

import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.jobs.periodic.PeriodicJob

/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 * @deprecated
 */
@UniversityComponent
class DisabledJob extends Counter implements PeriodicJob{

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
		return false
	}
}
