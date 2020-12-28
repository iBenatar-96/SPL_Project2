package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.Terminate;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;
    private Diary diary;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(Terminate.class, broadcast -> { //Subscribing to Terminate Broadcast, and setting set of actions to execute when called
            diary.setLandoTerminate();
            this.terminate();
        });
        this.subscribeEvent(BombDestroyerEvent.class, event -> { //Subscribing to BombDestroyerEvent and setting a set of avtions to execute when called
            try {
                Thread.sleep(this.duration); //Bombing the death star (executed by sleeping)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            complete(event, true); //Resolving future (completing event)
        });
    }
}
