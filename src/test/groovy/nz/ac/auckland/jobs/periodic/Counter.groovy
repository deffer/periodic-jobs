package nz.ac.auckland.jobs.periodic

class Counter {
	private int count = 0

	protected void execute(){
		count ++
	}

	public int getCount(){
		return count
	}
}
