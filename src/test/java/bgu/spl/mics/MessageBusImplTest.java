package bgu.spl.mics;

import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.Terminate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageBusImplTest {

    /* Initializing Objects for testing */
    MessageBus messageBus;
    MicroService m;
    Event<Boolean> event;
    Broadcast broadcast;
    Future<Boolean> futureObject;

    @BeforeEach
    public void setUp() {

        messageBus = MessageBusImpl.getInstance();
        event = new DeactivationEvent();
        broadcast = new Terminate();
        futureObject = new Future<>();

        // Initializing array of 2 objects to test methods on them
        Boolean[] called = new Boolean[2];
        called[0] = false; called[1] = false;

        // Set up MicroService Thread and start him
        m = new MicroService("test") {
            @Override
            protected void initialize() {
                subscribeEvent(event.getClass(), e -> {called[0] = true; terminate();});
                subscribeBroadcast(broadcast.getClass(), b -> {called[1] = true; terminate();});
                futureObject = messageBus.sendEvent(event);
            }
        };

        Thread t = new Thread(m); t.start();
        try { t.join(); }
            catch (InterruptedException ex) { ex.printStackTrace(); }
    }

    @Test
    public void testSubscribeBroadcast()
    {
        messageBus.register(m); //Testing register
        messageBus.subscribeBroadcast(broadcast.getClass(), m); //Testing subscribeBroadcast
        messageBus.sendBroadcast(broadcast); //Testing sendBroadcast
        try {
            Message test = messageBus.awaitMessage(m);
            assertEquals(test.getClass(),broadcast.getClass());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSubscribeEvent() {
        messageBus.register(m); //Testing register
        messageBus.subscribeEvent((Class<? extends Event<Boolean>>)event.getClass(), m); //Testing subscribeEvent
        messageBus.sendEvent(event); //Testing sendEvent
        try {
            Message test = messageBus.awaitMessage(m); //Testing awaitMessage
            assertEquals(test.getClass(), event.getClass());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testComplete() {
    messageBus.complete(event, true); //Testing complete
    Object resolved = futureObject.get();
    assertEquals(resolved, true);
    }

    @Test
    public void testAll(){
        Broadcast broadcast = new Terminate();
        Boolean[] called = new Boolean[2];
        called[0] = false;
        called[1] = false;

        MicroService m1 = new MicroService("test1") {
            @Override
            protected void initialize() {
                subscribeBroadcast(broadcast.getClass(), broad -> {called[0] = true; terminate();});
            }
        };
        MicroService m2 = new MicroService("test2")
        {
            @Override
            protected void initialize()
            {
                subscribeBroadcast(broadcast.getClass(), broad -> {called[1] = true; terminate();});
                MB.sendBroadcast(broadcast);
            }
        };

        Thread t1 = new Thread(m1); t1.start();
        Thread t2 = new Thread(m2); t2.start();
        try {
            t1.join(); t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(called[0] && called[1]);
    }

}
