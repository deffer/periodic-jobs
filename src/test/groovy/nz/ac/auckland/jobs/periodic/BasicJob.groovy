package nz.ac.auckland.jobs.periodic

import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.jobs.periodic.PeriodicJob


@UniversityComponent
class BasicJob implements PeriodicJob{

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
		return true
	}
}
