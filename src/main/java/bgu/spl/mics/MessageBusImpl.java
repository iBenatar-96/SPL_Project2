package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	//------Private-Fields-------
	public static Object lock;
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> microServiceToQueue;
	private ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> typeToMicroServiceQueue;
	private ConcurrentHashMap<Event<?>, Future<?>> eventToFuture;
	private ConcurrentMap<MicroService, ConcurrentLinkedQueue<Future<?>>> microServiceToFuture;

	//--------Initialization-------
	public MessageBusImpl(){
		lock = new Object();
		microServiceToQueue = new ConcurrentHashMap<>();
		typeToMicroServiceQueue = new ConcurrentHashMap<>();
		eventToFuture = new ConcurrentHashMap<>();
		microServiceToFuture = new ConcurrentHashMap<>();
	}
	//------Creating one instance of MessageBug-------
	private static MessageBusImpl MBI_instance = null;
	public static MessageBusImpl getInstance(){
		if (MBI_instance == null)
			MBI_instance = new MessageBusImpl();
		return MBI_instance;
	}


	/**
	 * Subscribes {@code m} to receive {@link Event}s of type {@code type}.
	 * <p>
	 * @param <T>  The type of the result expected by the completed event.
	 * @param type The type to subscribe to,
	 * @param m    The subscribing micro-service.
	 */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		typeToMicroServiceQueue.putIfAbsent(type, new ConcurrentLinkedQueue<>()); //Creating new CLQ in typeToMS hashmap
		typeToMicroServiceQueue.get(type).add(m); //Adding MicroService m to the queue
	}

	/**
	 * Subscribes {@code m} to receive {@link Broadcast}s of type {@code type}.
	 * <p>
	 * @param type 	The type to subscribe to.
	 * @param m    	The subscribing micro-service.
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		typeToMicroServiceQueue.putIfAbsent(type, new ConcurrentLinkedQueue<>()); //Creating new CLQ in typeToMS hashmap
		typeToMicroServiceQueue.get(type).add(m); //Adding MicroService m to the queue
	}

	/**
	 * Notifies the MessageBus that the event {@code e} is completed and its
	 * result was {@code result}.
	 * When this method is called, the message-bus will resolve the {@link Future}
	 * object associated with {@link Event} {@code e}.
	 * <p>
	 * @param <T>    The type of the result expected by the completed event.
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> toChange = (Future<T>) eventToFuture.get(e); //Getting future from eventToFuture hashmap
		if(toChange != null)
			toChange.resolve(result); //Resolving
	}

	/**
	 * Adds the {@link Broadcast} {@code b} to the message queues of all the
	 * micro-services subscribed to {@code b.getClass()}.
	 * <p>
	 * @param b 	The message to added to the queues.
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (lock){
		ConcurrentLinkedQueue<MicroService> toBroadcast = typeToMicroServiceQueue.get(b.getClass()); //Getting CLQ from typeToMSqueue
		if (toBroadcast != null) {
			for (MicroService m : toBroadcast) { //Adding the broadcast to every MS registered
				microServiceToQueue.get(m).add(b);
			}
			lock.notifyAll(); //Notifying everyone waiting, that there is a new broadcast
		}
		}
	}

	/**
	 * Adds the {@link Event} {@code e} to the message queue of one of the
	 * micro-services subscribed to {@code e.getClass()} in a round-robin
	 * fashion. This method should be non-blocking.
	 * <p>
	 * @param <T>    	The type of the result expected by the event and its corresponding future object.
	 * @param e     	The event to add to the queue.
	 * @return {@link Future<T>} object to be resolved once the processing is complete,
	 * 	       null in case no micro-service has subscribed to {@code e.getClass()}.
	 */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (lock) {
			if(typeToMicroServiceQueue.getOrDefault(e.getClass(),null)!=null && typeToMicroServiceQueue.get(e.getClass()).size()>0) { //Checking if it is valid, to prevent exceptions
				MicroService m = typeToMicroServiceQueue.get(e.getClass()).poll(); //Retrieving MicroService to sendEvent to
				if (m != null) {
					microServiceToQueue.get(m).add(e); //Adding the event to m's queue
					typeToMicroServiceQueue.get(e.getClass()).add(m); //Adding the MS to the back of the queue, to keep a round-robin state
					Future<T> futureToAdd = new Future<>();
					eventToFuture.putIfAbsent(e, futureToAdd); //Adding event to eventToFuture hashmap
					microServiceToFuture.putIfAbsent(m, new ConcurrentLinkedQueue<>());
					microServiceToFuture.get(m).add(futureToAdd);
					lock.notifyAll(); //Notifying everyone waiting, that there is a new Event
					return futureToAdd;
				}
			}
		}
		return null; //Returning null if conditions werent met
	}

	/**
	 * Allocates a message-queue for the {@link MicroService} {@code m}.
	 * <p>
	 * @param m the micro-service to create a queue for.
	 */
	@Override
	public void register(MicroService m) {
		microServiceToQueue.putIfAbsent(m, new LinkedBlockingQueue<>()); //Adding new LBQ to MStoQueue hashmap
	}

	/**
	 * Removes the message queue allocated to {@code m} via the call to
	 * {@link #register(bgu.spl.mics.MicroService)} and cleans all references
	 * related to {@code m} in this message-bus. If {@code m} was not
	 * registered, nothing should happen.
	 * <p>
	 * @param m the micro-service to unregister.
	 */
	@Override
	public void unregister(MicroService m) {
		synchronized (m) {
			LinkedBlockingQueue<Message> temp = microServiceToQueue.getOrDefault(m,null); //Clearing m's LBQ if exists
			if(temp!=null) {
				temp.clear();
				microServiceToQueue.remove(m); //Removing m's LBQ from hashmap
			}
			for (Class<? extends Message> classKeys : typeToMicroServiceQueue.keySet()) { //Clearing m's appearances in typeToMSqueue
				while (typeToMicroServiceQueue.get(classKeys).contains(m)) {
					typeToMicroServiceQueue.get(classKeys).remove(m);
				}
			}
			ConcurrentLinkedQueue<Future<?>> temp3 = microServiceToFuture.getOrDefault(m,null); //Clearing m's appearance in MStoFuture hashmap
			if(temp3!=null)
				temp3.clear();
				microServiceToFuture.remove(m); //Removing from hashmap
		}
	}


	/**
	 * Using this method, a <b>registered</b> micro-service can take message
	 * from its allocated queue.
	 * This method is blocking meaning that if no messages
	 * are available in the micro-service queue it
	 * should wait until a message becomes available.
	 * The method should throw the {@link IllegalStateException} in the case
	 * where {@code m} was never registered.
	 * <p>
	 * @param m The micro-service requesting to take a message from its message
	 *          queue.
	 * @return The next message in the {@code m}'s queue (blocking).
	 * @throws InterruptedException if interrupted while waiting for a message
	 *                              to became available.
	 */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		LinkedBlockingQueue<Message> queue = microServiceToQueue.getOrDefault(m, null); //Retrieving queue if exists
		if (queue == null)
			throw new IllegalStateException();
		synchronized (lock) {
			while (queue.isEmpty()) {
				try{
					lock.wait(); //Waiting while queue is empty
				}
				catch (InterruptedException e){}
			}
		}
		return queue.take(); //Retrieving the message
	}
}