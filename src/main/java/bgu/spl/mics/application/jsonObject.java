package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.Attack;


public class jsonObject {
    private  Attack[] attacks;
    private long R2D2;
    private long Lando;
    private int Ewoks;

    public jsonObject(){
        attacks= new Attack[0];
        R2D2 = 0;
        Lando = 0;
        Ewoks = 0;
    }
    public Attack[] getAttack(){
        return attacks;
    }

    public long getR2D2(){
        return R2D2;
    }
    public long getLando(){
        return Lando;
    }
    public int getEwoks(){
        return Ewoks;
    }

}