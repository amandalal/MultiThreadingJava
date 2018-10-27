import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleThreadPool {

    public static void main(String[] args) {
	
		int poolSize = 5;
		int taskNumber = 10;
		int sleepTime = 5000;
		
		long startTime = 0;
		long stopTime = 0;
		
        if (args != null && args.length == 3) {
			poolSize = Integer.parseInt(args[0]);
			taskNumber = Integer.parseInt(args[1]);
			sleepTime = Integer.parseInt(args[2]);
		}
	
		System.out.println ("Usage: java SimpleThreadPool <pool size> <number of tasks> <sleep time>");
		System.out.println ("  Example: java SimpleThreadPool 5 10 5000");
		System.out.println ("  Default values:");
		System.out.println ("  - pool size: 5");
		System.out.println ("  - number of tasks: 10");
		System.out.println ("  - sleep time: 5000");
		System.out.println ("\n");
		
		System.out.println ("Starting SimpleThreadPool with the following parameters:");
		System.out.println ("********************************************************");
		System.out.println ("  - pool size: " + poolSize);
		System.out.println ("  - number of tasks: " + taskNumber);
		System.out.println ("  - sleep time: " + sleepTime);
		System.out.println ("********************************************************");
		System.out.println ("\n");
	
        startTime = System.currentTimeMillis();
		ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i < taskNumber; i++) {
            Runnable worker = new WorkerThread(sleepTime, ("" + i), sleepTime,3);
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("Finished all threads");
		stopTime = System.currentTimeMillis();
		System.out.println("Elapsed Time: " + (stopTime - startTime) + " ms - "+(stopTime - startTime)/1000 +" sg");
    }
}
