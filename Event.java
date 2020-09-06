package cs2030.simulator;

/**
* Abstract Event class which other events extend from.
* @param id Event's id
* @param time Event's start time
* @param endTime Event's end time
* @param status Event's status
* @param isGreedy Customer Event's Type (Greedy or Not) 
*/
public abstract class Event {

    protected final int id;
    protected final double time;
    protected int status;
    private int serverId;
    protected boolean bySelfCheck;
    protected boolean isGreedy;

    public static final int REST = -1;
    public static final int BACK = 0;
    public static final int ARRIVES = 1;
    public static final int SERVED = 2;
    public static final int LEAVES = 3;
    public static final int DONE = 4;
    public static final int WAITS = 5;

    public Event(int id, double time) {
        this.id = id;
        this.time = time;
    }

    public int getId() {
        return this.id;
    }

    public int getServerId() {
        return this.serverId;
    }

    public double getTime() {
        return this.time;
    }

    public int getStatus() {
        return this.status;
    }

    public boolean isGreedy() {
        return this.isGreedy;
    }

    /**
    * Returns the basic String representation of each Event.
    */
    @Override 
    public String toString() {
        if (isGreedy()) {
            return String.format("%.3f ", time) + id + "(greedy)";
        }
        return String.format("%.3f ", time) + id;
    }
}
