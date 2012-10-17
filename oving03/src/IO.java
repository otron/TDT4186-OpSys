
public class IO {
	/** yo yo wat up some stats yo */
	private Statistics stats;
	/** get some gu-gu-G.U.I. */
	private Gui gui;
	
	/** This is the queue containing processes wanting to get their filthy hands on some I/O */
	private Queue ioQueue;
	/** The process which currently has right of I/O */
	private Process activeProc = null;
	/** the time to wait (in ms) until the I/O becomes available */
	private long ioWait;
	
	/**
	 * This is the constructooorrrr. It ain't nuthin' fancy.
	 * @param statistics
	 * @param gui
	 * @param IOQueue
	 * @param IOWait
	 */
	public IO(Statistics statistics, Gui gui, Queue IOQueue, long IOWait) {
		this.stats = statistics;
		this.gui = gui;
		this.ioQueue = IOQueue;
		this.ioWait = IOWait;
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
	

}
