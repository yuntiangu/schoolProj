package cs2030.simulator;

/**
 * WaitEvent is the child class of Event, meaning that a 'wait' event has been generated.
 * @param serverId is the id of the server that is going to serve this event
 */
public class WaitEvent extends Event {

    private int serverId;
    
    /**
     * Constructor for WaitEvent.
     */
    public WaitEvent(int id, double time, int serverId, boolean bySelfCheck, boolean isGreedy) {
        super(id,time);
        this.serverId = serverId;
        status = WAITS;
        this.bySelfCheck = bySelfCheck;
        this.isGreedy = isGreedy;
    }

    @Override 
    public String toString() {
        if (!bySelfCheck) {
            return super.toString() + " waits to be served by server " + serverId;
        }
        return super.toString() + " waits to be served by self-check " + serverId;
    }
}
