package nz.ac.auckland.jobs.periodic

/**
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
@Privileged
@NamedJob("reindexingJob") // lets configure job in property file
@DefaultConfiguration(delay = 50) // if there is no property file (during development for instance)
class AnnotatedPeriodicVIPJob implements Job{

	@Override
	Runnable getRunnable() {
		return null
	}
}
