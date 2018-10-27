import java.sql.Timestamp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements; 

public class WorkerThread implements Runnable { 

	private String command; 
	private int processTime; 
	private int retrySleepTime; 
	private int retryMaxAttempts; 
	private long elapsedTimeMillis; 

	public WorkerThread(int processTime, String command, int retrySleepTime, int retryMaxAttempts) { 
			this.command=command; 
			this.processTime=processTime; 
			this.retrySleepTime=retrySleepTime; 
			this.retryMaxAttempts=retryMaxAttempts; 
	} 

	@Override 
	public void run() { 
			System.out.println(new Timestamp((new java.util.Date()).getTime()) + " - " + Thread.currentThread().getName()+" Start. Command = "+command); 
			long startTime = System.currentTimeMillis(); 

			processCommand(); 

			long stopTime = System.currentTimeMillis(); 
			long millis = stopTime - startTime; 
			elapsedTimeMillis = millis; 
			System.out.println(new Timestamp((new java.util.Date()).getTime()) + " - " + Thread.currentThread().getName()+" End. Command = "+command+" ["+millis+"ms]"); 
	} 

	private void processCommand() { 
			try { 
				// PUT HERE THE CODE OF THE COMMAND TO BE EXECUTED FOR EACH THREAD 
				//Thread.sleep(processTime);
/*
				String url = "http://www.yahoo.es";
				Document doc = Jsoup.connect(url).get();
		        Elements links = doc.select("a[href]"); 
	    		System.out.println (" * Found " + links.size() + " links in " + url);
*/
				long counter = 0; 

				long startTime = System.currentTimeMillis(); 

				while ((System.currentTimeMillis() - startTime) < processTime) { 
					counter++; 
					if (counter == Long.MAX_VALUE) { 
							counter=0; 
					} 
				} 

				} catch (Exception e) { 
					e.printStackTrace(); 
			} 
	} 

	public int getRetrySleepTime () { 
			return this.retrySleepTime; 
	} 

	public int getRetryMaxAttempts () { 
			return this.retryMaxAttempts; 
	} 
	
	public long getElapsedTimeMillis () { 
			return this.elapsedTimeMillis; 
	} 


	public void setRetryMaxAttempts (int data) { 
			this.retryMaxAttempts=data; 
	} 

	@Override 
	public String toString(){ 
			return this.command; 
	} 
}
