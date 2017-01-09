
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueueControl implements Runnable {

	private static LinkedList<User> queue;
	private long storedTime;
	private AtomicBoolean closeThread = new AtomicBoolean(false);
	private boolean isConnected = true;
	private LinkedList<String> log;
	
	public QueueControl(LinkedList<String> log){
		this.log = log;
	}
	
	private void printIfAble(String line){
		synchronized(log){
			if (log != null){
				log.addFirst(Domain.formatTime(new Date())+ " - " + line);
				if(log.size()>50){
					log.removeLast();
				}
			}
		}
	}

	private static LinkedList<User> getQueue() {
		if (queue == null) {queue = new LinkedList<User>();}
		return queue;
	}

	private long storeTime() {
		storedTime = Date.from(Instant.now()).getTime();
		return storedTime;
	}

	synchronized public void close() {
		closeThread.set(true);
		this.notify();
	}

	synchronized public void addUser(User user) {
		for(User item : getQueue()){
			if(item.getLogin().equals(user.getLogin()) && 
					item.getSite().toString().equals(user.getSite().toString())){
				return;
			}
		}
		getQueue().push(user);
		printIfAble("added user " + user.getLogin());
		this.notify();
	}

	synchronized public void removeUser(String login, String domain) {
		Iterator<User> itr = getQueue().iterator();
		User user;
		while (itr.hasNext()) {
			user = itr.next();
			if (user.getLogin().equals(login) && user.getSite().toString().equals(domain)) {
				itr.remove();
				printIfAble("removed user " + user.getLogin());
			}
		}
		this.notify();
	}
	
	synchronized public ArrayList<User> getList(){
		ArrayList<User> list = new ArrayList<User>();
		for(User user : getQueue()){
			list.add(new User(user));
		}
		return list;
	}

	synchronized public void run() {
		User user;
		while (true) {
			if (closeThread.get()) {break;}
			user = getQueue().peek();
			if (user == null) {
				printIfAble("Nothing on the queue, going to sleep indefinetly");
				try {this.wait(0);} catch (InterruptedException e) {}
			} else if (user.getTime() > storeTime()) {
				printIfAble("Need to wait, wake up in " + (user.getTime() - storedTime) / 60000 + " min");
				try {this.wait(user.getTime() - storedTime);} catch (InterruptedException e) {}
			} else {
				if(!isConnected){
					printIfAble("No internet, waiting 20 sec");
					try {this.wait(20*Domain.MSEC_IN_SEC);} catch (InterruptedException e) {}
				}
				switch(user.getSite().bump(user)){
				case Domain.OK:
					printIfAble("Updated user "+user.getName());
					isConnected = true;
					//updateList
					//updateConnection
					break;
				case Domain.WRONG_LOGIN: 
					printIfAble(user.getLogin()+" : Wrong Username and/or Password");
					getQueue().pop();
					break;
				case Domain.NO_TIME_VISIBLE: 
					printIfAble(user.getLogin()+" :Didn't click button");
					isConnected = false;
					break;
				case Domain.NO_INTERNET: 
					printIfAble("No internet connection");
					isConnected = false;
					//updateConnection
					break;
				case Domain.WRONG_XPATH:
					printIfAble("Wrong Code and/or Site Changed");
					getQueue().pop();
					break;
				default:break;
				}
				Collections.sort(getQueue());
			}
		}
		printIfAble("closing controller thread");
	}
}