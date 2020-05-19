package cs2030.simulator;

public class ServedEvent extends Event {
    /**
     * ServeEvent class which is a child class of Event class, which always have the status SERVED.
     * @param serverId is the id of the server that is going to serve this event
     */

    public int serverId;
    
    /**
     * Constructor for a Serve Event.
     */
    public ServedEvent(int id, double time, int serverId, boolean bySelfCheck, boolean isGreedy) {
        super(id, time);
        this.serverId = serverId;
        status = SERVED;
        this.bySelfCheck = bySelfCheck;
        this.isGreedy = isGreedy;
    }

    public int getServerId() {
        return this.serverId;
    }

    @Override 
    public String toString() {
        if (!bySelfCheck) {
            return super.toString() + " served by server " + this.serverId;
        } 
        return super.toString() + " served by self-check " + this.serverId;
    }
}

