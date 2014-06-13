package nz.ac.auckland.jobs.periodic

import groovy.transform.CompileStatic

/**
 * These jobs are put into single thread queue, to make sure they are never executed simultaneously.
 * For example periodic database update and periodic reindexing should never run together.
 *
 * @deprecated use Job with @Privileged instead
 */
@CompileStatic
interface QueuedPeriodicJob extends AbstractPeriodicJob{

}
