import java.sql.Timestamp; 
import java.util.concurrent.ArrayBlockingQueue; 
import java.util.concurrent.Executors; 
import java.util.concurrent.ThreadFactory; 
import java.util.concurrent.TimeUnit; 

public class WorkerPool {

	// Default parameter values 
	private static int poolCoreSize = 5;
	private static int poolMaxSize = 10; 
	private static int queueCapacity = 50; 
	private static int timeoutSecs = 50; 
	private static int taskNumber = 100;  
	private static int processTime = 5000; 
	private static int retrySleepTime = 5000; 
	private static int retryMaxAttempts = 5; 
	private static int initialSleep = 5; 
	private static int monitorSleep = 3; 

	private static long startTime = 0; 
	private static long stopTime = 0; 

	public static void main(String args[]) throws InterruptedException { 

		if (args != null && args.length == 10) {
			poolCoreSize = Integer.parseInt(args[0]);
			poolMaxSize = Integer.parseInt(args[1]);
			queueCapacity = Integer.parseInt(args[2]);
			timeoutSecs = Integer.parseInt(args[3]);
			taskNumber = Integer.parseInt(args[4]);
			processTime = Integer.parseInt(args[5]);
			retrySleepTime = Integer.parseInt(args[6]);
			retryMaxAttempts = Integer.parseInt(args[7]);
			initialSleep = Integer.parseInt(args[8]);
			monitorSleep = Integer.parseInt(args[9]);
		} else {
			System.out.println ("Not all parameters informed. Using default values");
			System.out.println ("");
			System.out.println ("Usage: java WorkerPool <pool core size> <pool max size> <queue capacity> <timeout (secs)> <number of tasks> <task process (ms)> <retry sleep (ms)> <retry max attempts> <initial sleep (secs)> <monitor sleep (secs)>");
			System.out.println ("  Example: java WorkerPool 10 15 20 50 500 5000 5000 5 5 3");
			System.out.println ("");
		}

		System.out.println (new Timestamp((new java.util.Date()).getTime()) + " - Waiting " + initialSleep + " secs to start..."); 
		Thread.sleep(initialSleep*1000); 

		printParameters ("Started");

		startTime = System.currentTimeMillis();

		//RejectedExecutionHandler implementation 
		RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl(); 
		//Get the ThreadFactory implementation to use 
		ThreadFactory threadFactory = Executors.defaultThreadFactory(); 
		//creating the ThreadPoolExecutor 
		MyThreadPoolExecutor executorPool = new MyThreadPoolExecutor(poolCoreSize, poolMaxSize, timeoutSecs, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueCapacity), threadFactory, rejectionHandler); 
		//start the monitoring thread 
		MyMonitorThread monitor = new MyMonitorThread(executorPool, monitorSleep); 
		Thread monitorThread = new Thread(monitor); 
		monitorThread.start(); 
		//submit work to the thread pool 
		for(int i=0; i<taskNumber; i++) { 
			executorPool.execute(new WorkerThread(processTime,"task-"+i,retrySleepTime,retryMaxAttempts)); 
		} 

		//shut down the pool 
		System.out.println(new Timestamp((new java.util.Date()).getTime()) + " - Shutting down executor pool..."); 
		executorPool.shutdown(); 
		System.out.println(new Timestamp((new java.util.Date()).getTime()) + " - " + executorPool.getTaskCount() + " tasks. No additional tasks will be accepted"); 
		//shut down the monitor thread 

		while (!executorPool.isTerminated()) { 
			System.out.println(new Timestamp((new java.util.Date()).getTime()) + " - Waiting for all the Executor to terminate"); 
			Thread.sleep(monitorSleep*1000); 
		} 

		System.out.println(new Timestamp((new java.util.Date()).getTime()) + " - Executor terminated"); 
		System.out.println(new Timestamp((new java.util.Date()).getTime()) + " - Shutting down monitor thread..."); 
		monitor.shutdown(); 
		System.out.println(new Timestamp((new java.util.Date()).getTime()) + " - Shutting down monitor thread... done"); 
		stopTime = System.currentTimeMillis();

		printParameters ("Finished");
		System.out.println ("Results:"); 
		System.out.println ("**************************************************"); 
		System.out.println ("  - Start time  : " + new Timestamp(startTime)); 
		System.out.println ("  - Stop time   : " + new Timestamp(stopTime)); 

		long millis = stopTime - startTime;
		long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days); 
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes); 
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		System.out.println ("  - Elapsed time: " + (stopTime - startTime) + " ms - (" + hours + " hrs " + minutes + " min " + seconds + " secs)"); 
		System.out.println ("**************************************************"); 
		System.out.println ("  - Min elapsed execution time: " + executorPool.getMinExecutionTime() + " ms"); 
		System.out.println ("  - Max elapsed execution time: " + executorPool.getMaxExecutionTime() + " ms"); 
		System.out.println ("  - Avg elapsed execution time: " + (executorPool.getMaxExecutionTime() + executorPool.getMinExecutionTime())/2 + " ms");
		System.out.println ("**************************************************"); 
			
	}
	
	private static void printParameters (final String title) {
		System.out.println ("");
		System.out.println ("**************************************************"); 
		System.out.println (title + " WorkerPool with the following parameters:"); 
		System.out.println ("**************************************************"); 
		System.out.println ("  - pool core size       : " + poolCoreSize); 
		System.out.println ("  - pool max size        : " + poolMaxSize); 
		System.out.println ("  - queue capacity       : " + queueCapacity); 
		System.out.println ("  - timeout (secs)       : " + timeoutSecs); 
		System.out.println ("  - number of tasks      : " + taskNumber); 
		System.out.println ("  - task process (ms)    : " + processTime); 
		System.out.println ("  - retry sleep (ms)     : " + retrySleepTime); 
		System.out.println ("  - retry max attempts   : " + retryMaxAttempts);
		System.out.println ("  - initial sleep (secs) : " + retryMaxAttempts); 
		System.out.println ("  - monitor sleep (secs) : " + monitorSleep); 
		System.out.println ("**************************************************");
	}
} 
