# Periodic jobs Changelog

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
