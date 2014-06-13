package nz.ac.auckland.jobs.periodic

/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
@DefaultConfiguration(enabled = false)
class AnnotatedDisabledJob implements Job{
	int count = 0

	protected void execute(){
		count ++
	}

	@Override
	Runnable getRunnable() {
		return this.&execute;
	}
}
