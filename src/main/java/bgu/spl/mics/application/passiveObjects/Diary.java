package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    //-----Private-Fields-------
    private AtomicInteger totalAttacks;
    private Long HanSoloFinish;
    private Long C3POFinish;
    private Long R2D2Deactivate;
    private Long LeiaTerminate;
    private Long HanSoloTerminate;
    private Long C3POTerminate;
    private Long R2D2Terminate;
    private Long LandoTerminate;

    public Diary() {
        totalAttacks = new AtomicInteger(0);
    }

    private static Diary DY_instance = null;

    public static Diary getInstance() {
        if (DY_instance == null)
            DY_instance = new Diary();
        return DY_instance;
    }

    //--------Setters---------
    public void setHanSoloFinish() {
        HanSoloFinish = System.currentTimeMillis();
    }
    public void setC3POFinish() {
        C3POFinish = System.currentTimeMillis();
    }
    public void setR2D2Deactivate() {
        R2D2Deactivate = System.currentTimeMillis();
    }
    public void setLeiaTerminate() {
        LeiaTerminate = System.currentTimeMillis();
    }
    public void setHanSoloTerminate() {
        HanSoloTerminate = System.currentTimeMillis();
    }
    public void setC3POTerminate() {
        C3POTerminate = System.currentTimeMillis();
    }
    public void setR2D2Terminate() {
        R2D2Terminate = System.currentTimeMillis();
    }
    public void setLandoTerminate() {
        LandoTerminate = System.currentTimeMillis();
    }

    //-------Getters---------
    public AtomicInteger getTotalAttacks() {
        return totalAttacks;
    }

    public Long getHanSoloFinish() {
        return HanSoloFinish;
    }
    public Long getC3POFinish() {
        return C3POFinish;
    }
    public Long getLeiaTerminate() {
        return LeiaTerminate;
    }
    public Long getR2D2Deactivate() {
        return R2D2Deactivate;
    }
    public Long getHanSoloTerminate() {
        return HanSoloTerminate;
    }
    public Long getC3POTerminate() {
        return C3POTerminate;
    }
    public Long getLandoTerminate() {
        return LandoTerminate;
    }
    public Long getR2D2Terminate() {
        return R2D2Terminate;
    }
    public void resetNumberAttacks() {
        totalAttacks = new AtomicInteger(0);
    }
}
