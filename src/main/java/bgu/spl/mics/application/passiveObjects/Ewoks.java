package bgu.spl.mics.application.passiveObjects;
import java.util.ArrayList;
import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {

    private List<Ewok> ewoksList = new ArrayList<>(); //Holding all Ewoks in an ArrayList
    public static Object lock = new Object();

    public Ewoks(){ }


    private static Ewoks EWK_instance = null;
    public static Ewoks getInstance(){ //Creating instance on Ewoks
        if (EWK_instance == null)
            EWK_instance = new Ewoks();
        return EWK_instance;
    }

    //---------Setters-----------
    public void setEwoksList (int numOfEwoks){ //Setting EwoksList
        for (int i=1; i <= numOfEwoks; i++) {
            ewoksList.add(new Ewok(i));
        }
    }
    public void addEwok(Ewok e){
        this.ewoksList.add(e);
        e.acquire();
    }
    public void releaseAll() { //Releasing all acquired Ewoks
        synchronized (lock) {
            for (int i = 0; i < this.ewoksList.size(); i++) {
                ewoksList.get(i).release();
            }
            lock.notifyAll(); //Notifying everyone waiting on this lock that there are free ewoks
        }
    }

    //--------Getters----------
    public List<Ewok> getEwoksList(){
        return this.ewoksList;
    }

    public Ewoks getEwoks(List<Integer> list) { //Tryign to acquire Ewoks for AttackEvent
        synchronized (lock) {
            int index = 0;
            Ewoks toReturn = new Ewoks();
            while (index < list.size()) {
                while (!(ewoksList.get(list.get(index) - 1).available)) { //Checking availability
                    toReturn.releaseAll(); //If not avaible, free all Ewoks acquired so far
                    toReturn = new Ewoks(); //Initializing new list
                    index = 0;
                    try {
                        lock.wait(); //Waiting for Ewoks to be released
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                toReturn.addEwok(ewoksList.get(list.get(index) - 1)); //If available, acquire the Ewok
                index++;
            }
            return toReturn;
        }
    }

    public int getEwoksSize(){
        return this.ewoksList.size();
    }
}
