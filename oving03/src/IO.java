
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

}
