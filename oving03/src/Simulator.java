import java.io.*;

/**
 * The main class of the P3 exercise. This class is only partially complete.
 */
public class Simulator implements Constants
{
	/** The queue of events to come */
    private EventQueue eventQueue;
	/** Reference to the memory unit */
    private Memory memory;
	/** Reference to the GUI interface */
	private Gui gui;
	/** Reference to the statistics collector */
	private Statistics statistics;
	/** The global clock */
    private long clock;
	/** The length of the simulation */
	private long simulationLength;
	/** The average length between process arrivals */
	private long avgArrivalInterval;
	// Add member variables as needed
	/** REFERENCE TO THE CPU-THING */
	private CPU cpu;
	/** get some I/O mm yeah sex joke */
	private IO io;

	/**
	 * Constructs a scheduling simulator with the given parameters.
	 * @param memoryQueue			The memory queue to be used.
	 * @param cpuQueue				The CPU queue to be used.
	 * @param ioQueue				The I/O queue to be used.
	 * @param memorySize			The size of the memory.
	 * @param maxCpuTime			The maximum time quant used by the RR algorithm.
	 * @param avgIoTime				The average length of an I/O operation.
	 * @param simulationLength		The length of the simulation.
	 * @param avgArrivalInterval	The average time between process arrivals.
	 * @param gui					Reference to the GUI interface.
	 */
	public Simulator(Queue memoryQueue, Queue cpuQueue, Queue ioQueue, long memorySize,
			long maxCpuTime, long avgIoTime, long simulationLength, long avgArrivalInterval, Gui gui) {
		this.simulationLength = simulationLength;
		this.avgArrivalInterval = avgArrivalInterval;
		this.gui = gui;
		statistics = new Statistics();
		eventQueue = new EventQueue();
		memory = new Memory(memoryQueue, memorySize, statistics);
		clock = 0;
		// Add code as needed
		//initialize dat cpu yo
		this.cpu = new CPU(cpuQueue, maxCpuTime, this.statistics, this.gui);
		//wat up IOOOO
		this.io = new IO(this.statistics, this.gui, ioQueue, avgIoTime);
    }

    /**
	 * Starts the simulation. Contains the main loop, processing events.
	 * This method is called when the "Start simulation" button in the
	 * GUI is clicked.
	 */
	public void simulate() {
		// TODO: You may want to extend this method somewhat.
		
		/**
		 * checkit we got comments	
		 * Alright hold up now.
		 * What'cha all mean 'bout "experimenting with the Round Robin algorithm"? Shit nigger that all-gor-rythm is defined in a book somewhere, probably.
		 * Y'all be meanin' to say "changing about the value of the parameters of the algorithm?"
		 * 'Cause I see that as two sort-o' different things.
		 * 
		 * Anyway lemme just go on about assuming y'all be meanin' we be s'posed to mix up those values.
		 * Which is basically just the length of the CPU-time-quantum alloted to each process.
		 */

		System.out.print("Simulating...");
		// Genererate the first process arrival event
		eventQueue.insertEvent(new Event(NEW_PROCESS, 0));
		// Process events until the simulation length is exceeded:
		while (clock < simulationLength && !eventQueue.isEmpty()) {
			// Find the next event
			Event event = eventQueue.getNextEvent();
			// Find out how much time that passed...
			long timeDifference = event.getTime()-clock;
			//System.out.println("Clock:"+clock+"::"+eventQueue.isEmpty()+"::"+timeDifference);
			// ...and update the clock.
			clock = event.getTime();
			// Let the memory unit and the GUI know that time has passed
			memory.timePassed(timeDifference);
			gui.timePassed(timeDifference);
			io.updateQueueTime(timeDifference);
			cpu.updateTime(timeDifference);
			// Deal with the event
			if (clock < simulationLength) {
				processEvent(event);
			}

			// Note that the processing of most events should lead to new
			// events being added to the event queue!

		}
		System.out.println("..done.");
		// End the simulation by printing out the required statistics
		statistics.printReport(simulationLength);
	}

	/**
	 * Processes an event by inspecting its type and delegating
	 * the work to the appropriate method.
	 * @param event	The event to be processed.
	 */
	private void processEvent(Event event) {
		switch (event.getType()) {
			case NEW_PROCESS:
				createProcess();
				break;
			case SWITCH_PROCESS:
				switchProcess();
				break;
			case END_PROCESS:
				endProcess();
				break;
			case IO_REQUEST:
				processIoRequest();
				break;
			case END_IO:
				endIoOperation();
				break;
		}
	}

	/**
	 * Simulates a process arrival/creation.
	 */
	private void createProcess() {
		// Create a new process
		Process newProcess = new Process(memory.getMemorySize(), clock);
		memory.insertProcess(newProcess);
		flushMemoryQueue();
		// Add an event for the next process arrival
		long nextArrivalTime = clock + 1 + (long)(2*Math.random()*avgArrivalInterval);
		eventQueue.insertEvent(new Event(NEW_PROCESS, nextArrivalTime));
		// Update statistics
		statistics.nofCreatedProcesses++;
    }

	/**
	 * Transfers processes from the memory queue to the ready queue as long as there is enough
	 * memory for the processes.
	 */
	private void flushMemoryQueue() {
		Process p = memory.checkMemory(clock);
		// As long as there is enough memory, processes are moved from the memory queue to the cpu queue
		while(p != null) {
			
			// TODO: Add this process to the CPU queue!
			this.cpu.insertProcessInQueue(p);
			// Process inserted into CPU-queue.
			// IS THE CPU IDLING???
			if (this.cpu.isIdle()) {
				this.switchProcess(); //WELL IT SHOULDN'T BE
			}
			// Also add new events to the event queue if needed
			//????
			// Since we have implemented the CPU and I/O, we're going to leave this commented out as we have no idea what we are doing. 
			//memory.processCompleted(p);
			
			// Try to use the freed memory:
			flushMemoryQueue();
			// Update statistics
			// This is done when a process is ended (or ends, whichever is more correct, y'know grammatically. or whatevs)

			// Check for more free memory
			p = memory.checkMemory(clock);
		}
	}

	/**
	 * Simulates a process switch.
	 */
	private void switchProcess() {
		Process currentProc = cpu.getActive();
		if (currentProc != null) {
			//ooh we are forcing the process to switch what fun!
			currentProc.leaveCPU(clock);
			this.statistics.numberOFForcedProcessSwitches++;
			this.cpu.insertProcessInQueue(currentProc);
			currentProc.enterCPUQueue(clock);
		}
		Process proc = this.cpu.start();
		if (proc != null) {
			proc.enterCPU(clock);
			if (proc.timeUntilIO() > this.cpu.getMax() && proc.getCPUTimeNeeded() > this.cpu.getMax()) {
				//If the time until the process next needs access to IO exceeds the maximum time it is allowed to stay on the CPU
				//AND
				//if the process requires the CPU for a longer period of time than the maximum time alloted for each process at a time
				//then a process switch is scheduled after CPU.max has passed
				this.eventQueue.insertEvent(new Event(SWITCH_PROCESS, clock + this.cpu.getMax()));
			}
			else if (proc.timeUntilIO() > proc.getCPUTimeNeeded()) {
				//If the process needs less time with the CPU than the time until it next requires IO
				//then an end process event is scheduled for when the process is done with the CPU
				this.eventQueue.insertEvent(new Event(END_PROCESS, clock + proc.getCPUTimeNeeded()));
			}
			else {
				//an IO Request event is scheduled for when the process needs access to IO
				this.eventQueue.insertEvent(new Event(IO_REQUEST, clock + proc.timeUntilIO()));
			}
			
		}
		//register this event as having happened yo yo yo
		//this.eventQueue.insertEvent(new Event(SWITCH_PROCESS, this.clock));
		// Incomplete OR IS IT???? 
	}

	/**
	 * Ends the active process, and deallocates any resources allocated to it.
	 */
	private void endProcess() {
		Process proc = this.cpu.getActive();
		if (proc != null) { //is there no process active? Unsure if this will ever happen but hey NPE's are for chumps.
			this.statistics.nofCompletedProcesses++;
			proc.leaveCPU(clock); //make it leave the CPU. CAST IT AWAY.
			proc.updateStatistics(this.statistics);
			proc.updateStatsForClosureOfEmotionalRelations(this.statistics);
			this.memory.processCompleted(proc); //free up that memory
		}
	}

	/**
	 * Processes an event signifying that the active process needs to
	 * perform an I/O operation.
	 */
	private void processIoRequest() {
		this.statistics.numberOFProcessedIOOperations++;
		//this.eventQueue.insertEvent(new Event(Constants.IO_REQUEST, clock));
		Process proc = this.cpu.getActive();
		if (proc != null) {
			proc.leaveCPU(clock);
			proc.enterIOQueue(clock);
			if (this.io.addProcess(proc)) {
				proc.enterIO(clock);
				this.eventQueue.insertEvent(new Event(Constants.END_IO, clock + io.getIOTime())); //this should work...
			}
			switchProcess();
		}
	}

	/**
	 * Processes an event signifying that the process currently doing I/O
	 * is done with its I/O operation.
	 */
	private void endIoOperation() {
		Process proc = this.io.getProcess();
		if (proc != null) {
			this.statistics.numberOFProcessedIOOperations++;
			proc.leavesIO(clock);
			this.cpu.insertProcessInQueue(proc);
			proc.enterCPUQueue(clock);
			
			if (this.cpu.isIdle())
				switchProcess(); //ain't got no time for idlin', boy.
			
			proc = this.io.begin();
			if (proc != null) {
				proc.enterIO(clock);
				this.eventQueue.insertEvent(new Event(Constants.END_IO, clock + io.getIOTime()));
			}
		}
	}
	

	/**
	 * Reads a number from the an input reader.
	 * @param reader	The input reader from which to read a number.
	 * @return			The number that was inputted.
	 */
	public static long readLong(BufferedReader reader) {
		try {
			return Long.parseLong(reader.readLine());
		} catch (IOException ioe) {
			return 100;
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	/**
	 * The startup method. Reads relevant parameters from the standard input,
	 * and starts up the GUI. The GUI will then start the simulation when
	 * the user clicks the "Start simulation" button.
	 * @param args	Parameters from the command line, they are ignored.
	 */
	public static void main(String args[]) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please input system parameters: ");

		System.out.print("Memory size (KB): ");
		long memorySize = readLong(reader);
		while(memorySize < 400) {
			System.out.println("Memory size must be at least 400 KB. Specify memory size (KB): ");
			memorySize = readLong(reader);
		}

		System.out.print("Maximum uninterrupted cpu time for a process (ms): ");
		long maxCpuTime = readLong(reader);

		System.out.print("Average I/O operation time (ms): ");
		long avgIoTime = readLong(reader);

		System.out.print("Simulation length (ms): ");
		long simulationLength = readLong(reader);
		while(simulationLength < 1) {
			System.out.println("Simulation length must be at least 1 ms. Specify simulation length (ms): ");
			simulationLength = readLong(reader);
		}

		System.out.print("Average time between process arrivals (ms): ");
		long avgArrivalInterval = readLong(reader);

		SimulationGui gui = new SimulationGui(memorySize, maxCpuTime, avgIoTime, simulationLength, avgArrivalInterval);
	}
	
	/** The discussion/comments regarding variations in the RR-algorithm are found here.
	 * THINGS THAT COULD BE CHANGED
	 * The maximum alloted CPU time for a process
	 * ...
	 * Aaand that's it.
	 * I mean, that's all there's to the round-robin algorithm.
	 * The rest is up to the hardware (I/O processing time (i.e. how long it takes for a keypress to get registered or whatevs), the time it takes for a process switch, and I guess other stuff)
	 * 
	 * This max alloted time needs to be sufficiently larger than the time it takes to switch a process, so that we don't end up spending a considerable amount of time just switching between processes.
	 * Also it needs to not be all that large because then the entire thing basically gets reduced to a FIFO-type-deal.
	 * Beyond that I guess we could change the system entirely on its head or something but then we wouldn't be using a round robin algorithm now would we?
	 * 
	 * 
	 */
}