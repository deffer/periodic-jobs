package nz.ac.auckland.simple

/**
 * Init jobs are executed only once and on the same thread as queued periodic jobs.
 */
public interface InitJob {

	/**
	 * Method to run
	 * @return
	 */
	Runnable getRunnable()

	/**
	 * Initial delay before first execution in seconds
	 * @return
	 */
	Long getInitialDelay()
}