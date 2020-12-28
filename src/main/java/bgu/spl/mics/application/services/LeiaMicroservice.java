package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.Terminate;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    //-----Private-Fields-------
	private Attack[] attacks;
	private Future[] futures;
	private int index;
	private Diary diary;

	//------Initialization------
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		this.futures = new Future[attacks.length];
		this.index = 0;
		diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        try {
            Thread.sleep(100); //Sleeping for 100ms so that MicroServices can register
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Attack a : attacks) { //Sending attacks from Attacks[]
            Future<Boolean> future = this.sendEvent(new AttackEvent(a));
            futures[index] = future; //Saving future in an array, so that we can check when they were all resolved (completed)
            index++;
        }
        for(int i=0; i< futures.length; i++){ //Waiting for all future (events) to be completed
            futures[i].get();
        }
        Future<Boolean> future = this.sendEvent(new DeactivationEvent());//Sending new DeactivationEvent
        while(future==null || !future.isDone()) {
            try {
                future.get();
            } catch (NullPointerException e) {
            }
        }
        Future<Boolean> future2 = this.sendEvent(new BombDestroyerEvent()); //Sending new BombDestroyerEvent
        while(future2==null || !future2.isDone()) {
            try {
                future2.get();
            } catch (NullPointerException e) {
            }
        }
        this.sendBroadcast(new Terminate()); //Send new broadcast to all microservices to terminate
        diary.setLeiaTerminate(); //Setting termination time
        this.terminate();
    }
}
