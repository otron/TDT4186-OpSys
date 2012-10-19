import java.util.Random;


public class IO {
	/** yo yo wat up some stats yo */
	private Statistics stats;
	/** get some gu-gu-G.U.I. */
	private Gui gui;
	
	/** This is the queue containing processes wanting to get their filthy hands on some I/O */
	private Queue ioQueue;
	/** The process which currently has right of I/O */
	private Process activeProc = null;
	private long avgIOTime;
	private long queueTime;
	
	/**
	 * This is the constructooorrrr. It ain't nuthin' fancy.
	 * @param statistics
	 * @param gui
	 * @param IOQueue
	 * @param avgIOWait
	 */
	public IO(Statistics statistics, Gui gui, Queue IOQueue, long avgIOTime) {
		this.stats = statistics;
		this.gui = gui;
		this.ioQueue = IOQueue;
		this.avgIOTime = avgIOTime;
		this.queueTime = 0;
	}
	
	/**
	 * hands over I/O access to the process that's first in the queue.
	 * @return returns the now-active process.
	 */
	public Process begin() {
		if (this.ioQueue.isEmpty())
			return null;
		this.activeProc = (Process) this.ioQueue.removeNext();
		this.gui.setIoActive(this.activeProc);
		return this.activeProc;
	}
	
	/**
	 * 
	 * @param p process to add in the IO Queue
	 * @return Returns true if the process immediately received I/O control or whatever. False if it didn't. Boo-fucking-hoo.
	 */
	public boolean addProcess(Process process) {
		this.ioQueue.insert(process);
		if (this.activeProc == null) {
			this.begin();
			return true;
		} else
			return false;
	}
	
	/**
	 * grabs the current active process and retracts it access to I/O
	 * @return returns the current active process.
	 */
	public Process getProcess() {
		Process proc = this.activeProc;
		this.gui.setIoActive((this.activeProc = null));
		return proc;
	}
	/**
	 * generates a random time interval for when the IO next becomes available, averaged around the avgIOWait time.
	 * @return the time (in ms) until the IO becomes available.
	 */
	public long getIOTime() {
		Random rng = new Random();
		return (long) (rng.nextDouble() * rng.nextDouble() * avgIOTime * 2);
	}
	
	/**
	 * updates the longest queue time in statistics if the current queue length is longer than the longest previously recorded.
	 * @param time the time that has passed
	 */
	public void updateQueueTime(long time) {
		long l = this.ioQueue.getQueueLength();
		this.stats.ioQueueLengthTime += l * time;
		if (l > stats.largestOccuringIOQueueLength) {
			stats.largestOccuringIOQueueLength = l;
		}
	}
}
