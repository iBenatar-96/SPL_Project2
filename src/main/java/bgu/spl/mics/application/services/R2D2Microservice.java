package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.Terminate;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private long duration;
    private Diary diary;
    public R2D2Microservice(long duration) {
        super("R2D2");
        diary = Diary.getInstance();
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(Terminate.class, broadcast ->{ //Subscribing to Terminate Broadcast, and setting set of actions to execute when called
            diary.setR2D2Terminate();
            this.terminate();
        });
        this.subscribeEvent(DeactivationEvent.class, event ->{ //Subscribing to DeactivationEvent and setting a set of avtions to execute when called
            try {
                Thread.sleep(this.duration); //Deactivating sheild (executed by sleeping)
                diary.setR2D2Deactivate(); //Setting deactivation time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            complete(event, true); //Resolving future (completing event)
        });
    }
}
