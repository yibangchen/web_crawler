package edu.upenn.cis455.crawler;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.*;

import edu.upenn.cis455.storage.*;

/**
 * Threadpool for handling requests
 * @author cis455
 *
 */

public class ThreadPool {
	
	private final int THREAD_NO = 100;
	private BlockingQueue<Object> queue;
	private int threadCount;
	private Worker[] workers;
	private Thread[] workerThreads;
	private volatile boolean shutdown = false;
	private static final Object KILL_ME = new Object();
	private boolean shutdownRequested = false;
	private AtomicLong totalParsedSize; 
	private CrawlHistoryDA accessor;
	private XmlDocumentDA xmlAccessor;
	private HtmlDocumentDA htmlAccessor;
	private RobotsDA robAccessor;
	private long maxSize;
	private Lock lock,incrementLock;
	private AtomicLong totalPagesRead;
	
	class BlockingQueue<item> {	
		private final Lock offerLock = new ReentrantLock();
		private final Lock takeLock = new ReentrantLock();
		private final Condition notEmpty = takeLock.newCondition();
		private final Condition notFull = offerLock.newCondition();
		private final AtomicLong count = new AtomicLong();
		private long capacity = Integer.MAX_VALUE;
		private ArrayList<item> items = new ArrayList<item>();
		
		public BlockingQueue(){
		}
		
		public boolean offer(item i) {
			
			final AtomicLong count = this.count;
			if(count.get() == capacity)
				return false;
			long c =-1;
			final Lock l = this.offerLock;
			l.lock();
			try{
				
				if(count.get() < capacity)
					insert(i);
				c = count.getAndIncrement();
				if(c+1 < capacity)
					notFull.signal();
				
			}finally{
					
				l.unlock();
				if(c==0)
					signalNotEmpty();
			}
			
							
			return c>=0;
		}
		
		public boolean add(item i) throws IllegalStateException{
			
			if(offer(i))
				return true;
			else
				throw new IllegalStateException("Queue at full capacity");
				
		}
		
		public item take() throws InterruptedException{
			
			final Lock l = this.takeLock;
			final AtomicLong count = this.count;
			item i;
			long c =-1;
			l.lockInterruptibly();
			try{
				while(count.get() == 0)
					notEmpty.await();
				 i = remove();
				 c = count.getAndDecrement();
				 if(c>1)
					 notEmpty.signal();
				
			}finally{
				
				l.unlock();
				if(c==capacity)
					signalNotFull();
			}
			 
			return i;
		}
		
		public void clear(){
			offerLock.lock();
			takeLock.lock();
			
			items.clear();
			
			offerLock.unlock();
			takeLock.unlock();
						
		}
		
		private void signalNotEmpty(){
			final Lock l = this.takeLock;
			l.lock();
			try{
				notEmpty.signal();
			}finally{
				l.unlock();
			}
		}
		
		private void signalNotFull(){
			final Lock l = this.offerLock;
			l.lock();
			try{
				notFull.signal();
			}finally{
				l.unlock();
			}
			
		}
		
		private void insert(item i){
			
			items.add(i);	
		}
		
		private item remove(){
			
			return items.remove(0);
		}
		
		public long getSize(){
			return count.get();
		}
		
		
	}
	
	public ThreadPool(int count, CrawlHistoryDA accessor,XmlDocumentDA xmlAccessor, HtmlDocumentDA htmlAccessor,long size, RobotsDA robAccessor) throws IllegalArgumentException{
		
		if(isValidCount(count))
			this.threadCount = count;
		else
			throw new IllegalArgumentException("Invalid Thread Count");
		
		workers = new Worker[count];
		workerThreads = new Thread[count];
		queue = new BlockingQueue<Object>();
		totalParsedSize = new AtomicLong(0);
		this.accessor = accessor;
		this.xmlAccessor = xmlAccessor;
		this.htmlAccessor = htmlAccessor;
		this.maxSize = size;
		this.robAccessor = robAccessor;
		this.lock = new ReentrantLock();
		this.incrementLock = new ReentrantLock();
		this.totalPagesRead = new AtomicLong(0);
		
		startThreads();
	}
	
	public boolean execute(Callable<Object> task){
		
		return this.queue.offer(task);
		
	}
	
	private void incrementSize(long size){
		
			incrementLock.lock();
			totalParsedSize.addAndGet(size);
			incrementLock.unlock();
		
	}
	
	public long getTotalParsedSize(){
		return this.totalParsedSize.longValue();
	}
	
	private void startThreads(){
		
		synchronized (this) {
		
			for(int i=0;i<threadCount;i++){
				
				this.workers[i] = new Worker();
				this.workerThreads[i] = new Thread(workers[i]);
				this.workerThreads[i].start();
			}
		}
	}
	
	public synchronized boolean shutDown(){
		
		this.shutdown = true;
		this.queue.add(KILL_ME);
			try{
				for(Worker w: workers){
					
					w.thread.interrupt();
					
				}
				
				queue.clear();
				
				for(Thread workerThread : workerThreads){
					
					workerThread.interrupt();
					
				}
				
				return true;
				
			}catch(Exception e){
				System.out.println(Thread.currentThread()+": Worker Thread Shutdown Failed");
				return false;
			}
	}
	
		
	private boolean isValidCount(int count){
		
		if(count>0)
			return true;
		else
			return false;
		
	} 
	
	public Thread[] getWorkerThreads(){
		return workerThreads;
	}
	
	public Worker[] getWorkers(){
		return workers;
	}
	
	public boolean isShutdownRequested() {
		return shutdownRequested;
	}

	public void setShutdownRequested(boolean shutdownRequested) {
		this.shutdownRequested = shutdownRequested;
	}
	
	public synchronized void isShutdownRequired(){
		lock.lock();
		boolean required=true;
		Thread[] threads = getWorkerThreads();
		loop:for(Thread t: threads){
			if(t!=Thread.currentThread()){
				if(t.getState()!=Thread.State.WAITING){
					required=false;
					break loop;
				}
			}
		}
		
		if(required){
			if(this.queue.getSize()!=0){
				required=false;
			}
		}
		
		if(required){
			System.out.println("No more links present. Shutting down.");
			
			shutdownRequested=true;
			
		}
		lock.unlock();
		
	}
	public long getTotalPagesRead() {
		return totalPagesRead.longValue();
	}

	public void setTotalPagesRead() {
		this.totalPagesRead.incrementAndGet();		
	}
/**
 * Worker class which executes the task
 * @author cis455
 *
 */
	 public class Worker implements Runnable{

		private final Thread thread;
		private String status = "Waiting"; 		
		
		public String getStatus(){
			return status;
		}
		
		private void setStatus(String s){
			this.status = s;
		}
		
		private Worker(){
			this.thread = Thread.currentThread();
		}
		
		@Override
		public void run() {
			try{
			 while(!shutdown){
					try{
						    Object r = queue.take();
							if(r == KILL_ME)
								return;
							else
								if(r instanceof Callable)
								{	
									if(r instanceof Crawler)
									{
										@SuppressWarnings("unchecked")
										Object  o = ((Callable<Object>) r).call();
										if(o==null){
											System.out.println("Null");
										}
										if(o instanceof HashMap<?, ?>)
										{	
											addLinkToProcessed(((Crawler) r).getCurrentURL());
											HashMap<String,Object> retVal = (HashMap<String,Object>)o;
											processAndAddToQueue(retVal);
										}		
									}
								}
								else
									System.out.println("Invalid Object received "+r.getClass()+".Only Runnable expected");
						   						
					}catch(InterruptedException interrupt){
						if(thread.isInterrupted())
							return;
					    System.out.println("Interrupted exception in: "+thread.getName());
					}				
				}
			}catch(Exception intr){
							
				this.thread.interrupt();
				
			}finally{
				isShutdownRequired();
				this.thread.interrupt();
			}
		}
		
		private void addLinkToProcessed(String link){
			CrawlHistory entity = new CrawlHistory();
			entity.setPageUrl(link);
			entity.setVisitedTime(new Date().getTime());
			accessor.putEntity(entity);
			setTotalPagesRead();
		}
		
		private void processAndAddToQueue(HashMap<String,Object> retVal){
			
			List<String> linkLists = (List<String>)retVal.get("Links");
			long value = (Long)retVal.get("Size");
			
			if(value>0){
				incrementSize(value);
			}
			
			if(linkLists!=null){
				for(String link:linkLists){
					CrawlHistory ent = accessor.fetchEntityFromPrimaryKey(link);
					if(ent==null){
						try{
							Crawler crawl = new Crawler(link, xmlAccessor, htmlAccessor, maxSize,robAccessor);
							execute(crawl);
						}catch(MalformedURLException mfue){
							System.out.println("Malformed URL: "+mfue.getMessage());
						}
					}
				}
			}
			isShutdownRequired();
						
		}	
	}
}
