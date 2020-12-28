package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.Terminate;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.Comparator;
import java.util.List;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
    //-----Private-Fields-----
    private Ewoks listOfEwoks;
    private Ewoks myEwoks;
    private int index;
    private Diary diary;

    //----Initialization-----
    public C3POMicroservice() {
        super("C3PO");
        listOfEwoks = Ewoks.getInstance();
        myEwoks = new Ewoks();
        index = 0;
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(Terminate.class, broadcast -> { //Subscribing to Terminate Broadcast, and setting set of actions to execute when called
            diary.setC3POTerminate();
            this.terminate();
        });
        this.subscribeEvent(AttackEvent.class, event -> { //Subscribing to AttackEvents, and setting set of actions to execute when called
            List<Integer> serialNum = event.getSer(); //List of Ewoks to acquire
            serialNum.sort(Comparator.naturalOrder());
            while(myEwoks.getEwoksSize()!=serialNum.size()) {
                myEwoks = listOfEwoks.getEwoks(serialNum); //Trying to acquire all ewoks, to execute the attack
                System.out.println("This is C3PO +" + System.currentTimeMillis());
            }
            try {
                Thread.sleep(event.getDur()); //If acquired, execute (sleep) the duration recquired in the event
                myEwoks.releaseAll(); //Releasing ewoks
                myEwoks = new Ewoks(); //Initializing new empty list of ewoks, for a new attack
                diary.getTotalAttacks().getAndIncrement(); //Increasing number of totalAttack executed
                complete(event,true); //Resolving the event
                diary.setC3POFinish(); //Setting finish time
            } catch (Exception ignored) {}
        });
    }
}


