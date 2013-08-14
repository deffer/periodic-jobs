package nz.ac.auckland.simple


public interface PeriodicJob {

	Runnable getRunnable()
	Long getInitialDelay()
	Long getPeriodicDelay()

}