# Periodic jobs Changelog

## 3.3

Deprecated:

- All interfaces are deprecated now

New:

- New interface for all jobs - Job
- Annotations to provide default configuration
- Job configuration is looked up in the System.properties

## 3.2

New:

- Can access job through execution log

Fixes:

- Fixed configuration

## 3.1

New:

- Job executions tracking
    - Start time
    - Finish time
    - Errors (if any)

Changes:

- Job is no longer cancelled if previous run resulted in uncaught exception.
- If Job is disabled, its still registered, but not scheduled.
- Abstract interfaces
- Test logging fixed


## 2.3

- Fix for spring config
- Some javadocs

## 2.2

- Project is renamed
- Packages are renamed

## 1.x

- Supports three type of jobs
- Supports disabling jobs (jobs are not scheduled if disabled)
- Can be disabled totally
