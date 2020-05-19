package cs2030.simulator;

/**
* Done Event is the child class of Event, meaning a done event has been generated.
* @param serverId is the ID of the server that has served this event.
*/
public class DoneEvent extends Event {

    public int serverId;
    
    /**
     * Constructor for DoneEvent.
     */
    public DoneEvent(int id, double time, int serverId, boolean bySelfCheck, boolean isGreedy) {
        super(id, time);
        this.serverId = serverId;
        status = DONE;
        this.bySelfCheck = bySelfCheck;
        this.isGreedy = isGreedy;
    }

    public int getServerId() {
        return this.serverId;
    }

    @Override 
    public String toString() {
        if (!bySelfCheck) {
            return super.toString() + " done serving by server " + serverId;
        }
        return super.toString() + " done serving by self-check " + serverId;
    }
}
