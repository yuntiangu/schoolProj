package cs2030.simulator;

import java.util.Queue;
import java.util.PriorityQueue;

/**
 * Server class to simulate the serving of the Events.
 * @param isServing to check if the server is serving or not.
 * @param nextTime the nextTime this Server is able to serve another Event.
 * @param qMax the maximum length each server's queue can hold.
 * @param line the server's respective queue.
 * @param waitThenServe the first waiting customer in the respective server's queue.
 * @param isResting the server rest state, true if resting, false if not resting.
 */
public class Server {

    protected int id;
    protected boolean isServing;
    protected double nextTime;
    protected int qMax;
    protected PriorityQueue<Event> line;
    protected Event waitThenServe;
    protected boolean isResting;

    /**
     * public Constructor for Server.
     */
    public Server(int id, int qMax) {
        this.id = id;
        this.qMax = qMax;
        this.line = new PriorityQueue<>(new EventComparator());
        this.isServing = false;
        this.isResting = false;
    }

    /**
     * Protected Constructor for immutability.
     */
    protected Server(int id, int qMax, boolean isServing, double nextTime,
            PriorityQueue<Event> updatedQueue, Event waiting, boolean isResting) { //toWait()
        this.id = id;
        this.qMax = qMax;
        this.isServing = isServing;
        this.nextTime = nextTime;
        this.line = updatedQueue;
        this.waitThenServe = waiting;
        this.isResting = isResting;
    }

    protected Server(int id, int qMax, boolean isServing,
            double nextTime, PriorityQueue<Event> updatedQueue, boolean isResting) { //for resting
        this.id = id;
        this.qMax = qMax;
        this.line = updatedQueue;
        this.isServing = isServing;
        this.nextTime = nextTime;
        this.isResting = isResting;
    }
    
    /**
     * to determine whether said Customer can be served by this server.
     */
    public boolean canServe(Event cus) {
        if (resting()) {
            return false;
        } 
        if (isServing) {
            return false;
        } 
        return !resting() && cus.getTime() >= nextTime;
    }   

    public boolean canHold() {
        return (isServing || isResting) && line.size() < qMax;
    } 

    public Server serve(Event cus, double serviceTime) {
        return new Server(this.id, this.qMax, true, cus.getTime() + serviceTime, this.line, false);
    }

    public Server hold(Event cus) {
        this.line.add(cus);
        return new Server(this.id, this.qMax, true, this.nextTime, this.line, this.isResting);
    }
    
    /**
     * helper method for this server to serve the first customer in this queue.
     */
    public Server serveWait(double serviceTime) {
        Event waitingCus = this.line.poll();
        return new Server(this.id, this.qMax, true, this.nextTime + serviceTime,
                this.line, waitingCus, false);
    }

    public Server toDone(double doneTime) {
        return new Server(this.id, this.qMax, false, doneTime, this.line, this.isResting);
    }

    public Server rest() throws Exception {
        return new Server(this.id, this.qMax, false, this.nextTime,
                this.line, true);
    }

    public Server backFromRest(double backAt) {
        return new Server(this.id, this.qMax, false, backAt,
                this.line, false);
    }

    public int getId() {
        return this.id;
    }

    public double serviceRate() {
        return this.serviceRate();
    }

    public double getNextServeTime() {
        return this.nextTime;
    }

    public Event getWaiting() {
        return this.waitThenServe;
    }

    public boolean stillGotWaiting() {
        return !this.line.isEmpty();
    }

    public boolean resting() {
        return this.isResting;
    }

    public int queueLength() {
        return this.line.size();
    }

}
