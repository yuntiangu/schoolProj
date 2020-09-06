package cs2030.simulator;

/**     
 * LeaveEvent is the child class of Event, meaning that a 'leave' event has been generated.
 */
public class LeaveEvent extends Event {

    /**
     * Constructor for LeaveEvent.
     */
    public LeaveEvent(int id, double time, boolean isGreedy) {
        super(id, time);
        status = LEAVES;
        this.isGreedy = isGreedy;
    }

    @Override
    public String toString() {
        return super.toString() + " leaves";
    }
}
