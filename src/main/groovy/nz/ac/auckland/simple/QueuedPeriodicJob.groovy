package nz.ac.auckland.simple

/**
 * These jobs are put into single thread queue, to make sure they are never executed simultaneously.
 * For example periodic database update and periodic reindexing should never run together
 */
interface QueuedPeriodicJob {
	Runnable getRunnable()
	Long getInitialDelay()
	Long getPeriodicDelay()
}
