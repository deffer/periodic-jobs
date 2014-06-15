package nz.ac.auckland.jobs.periodic

import nz.ac.auckland.common.stereotypes.UniversityComponent

/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 * @deprecated
 */
@UniversityComponent
class BrokenJob extends Counter implements PeriodicJob{

	static long waitTime = 1100

	protected void execute(){
		super.execute()
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
