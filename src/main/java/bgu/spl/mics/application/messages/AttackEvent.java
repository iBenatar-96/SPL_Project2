package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.List;

public class AttackEvent implements Event<Boolean> {

    private Attack attack; //Holding an event as a private field

    //-----Constructor------
    public AttackEvent(Attack a){ //Initializing new AttackEvent
        this.attack = a;
    }
    //------Getters---------
    public int getDur(){
        return this.attack.getDuration();
    }
    public List<Integer> getSer(){
        return this.attack.getSerials();
    }
    public Attack getEvent(){
        return this.attack;
    }
}
