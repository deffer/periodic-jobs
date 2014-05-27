package nz.ac.auckland.jobs.periodic

import nz.ac.auckland.common.stereotypes.UniversityComponent

@UniversityComponent
class BrokenJob implements PeriodicJob{

	int count = 0

	static long waitTime = 1100

	protected void execute(){
		count ++
		Thread.sleep(waitTime)
		throw new NullPointerException('Ooops...')
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
