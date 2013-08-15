package nz.ac.auckland.simple

/**
 * Init jobs are executed only once and on the same thread as queued periodic jobs.
 */
public interface InitJob {
	Runnable getRunnable()
	Long getInitialDelay()
}