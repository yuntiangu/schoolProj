package cs2030.simulator;

/**     
* ArriveEvent is a child class of Event, meaning that a 'arrive' event has been generated.
*/
public class ArriveEvent extends Event {
    
    /**
     * Constructor for ArriveEvent.
     */
    public ArriveEvent(int id, double time, boolean isGreedy) {
        super(id,time);
        status = ARRIVES;
        this.isGreedy = isGreedy;
    }
    
    @Override
    public String toString() {
        return super.toString() + " arrives"; 
    }
}

