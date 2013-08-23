package nz.ac.auckland.simple

import nz.ac.auckland.common.stereotypes.UniversityComponent

@UniversityComponent
class DisabledJob  implements PeriodicJob{

	int count = 0

	protected void execute(){
		count ++
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
		return false
	}
}
