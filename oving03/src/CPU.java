
public class CPU {
	
	Queue queue;
	Statistics statistics;
	Process active;
	Gui graphcs;
	int max;
	
	// Konstrukt¿r med argumenter
	public CPU (Queue queue, int max, Statistics statistics, Gui gui) {
		this.queue = queue;
		this.statistics = statistics;
		this.graphcs = gui;
		this.max = max;
    }
	
	
	public void insertProcessInQueue(Process p) {
		queue.insert(p);
	}
	
	public boolean isIdle() {
		return this.active == null;
	}
	
	public Process getActive() {
		Process p = this.active;
		this.active = null;
		return p;
	}
	
	public int getMax() {
		return max;
	}
	
	public Process start() {
		if (!this.queue.isEmpty()) {
			Process p = (Process) this.queue.removeNext();
			this.active = p;
			graphcs.setCpuActive(p);
			return p;
		}
		
		else {
			this.active = null;
			graphcs.setCpuActive(null);
			return null;
		}
	}
	
	
	public void updateTime (int timePassed) {
		this.statistics.cpuQueueLengthTime += this.queue.getQueueLength() * timePassed;
		
		if (this.queue.getQueueLength() > this.statistics.getCpuQueueLargestLength)
			this.statistics.cpuQueueLargestLength = this.queue.getQueueLength();
	}
	
}
