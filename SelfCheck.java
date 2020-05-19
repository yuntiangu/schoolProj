package cs2030.simulator;

import java.util.PriorityQueue;

/**
 * SelfCheck is the self-checkout counter, a child class of server.
 * A self-checkout counter never rests.
 * Note: a Self-Checkout Counter cannot rest
 */

public class SelfCheck extends Server {
    public static PriorityQueue<Event> selfCheckLine = new PriorityQueue<>(new EventComparator());
    public static int selfQMax;

    public SelfCheck(int id, int qMax) {
        super(id,qMax);
        selfQMax = qMax;
    }

    public SelfCheck(int id, int qMax, boolean isServing, double nextTime,
            PriorityQueue<Event> updatedQueue, Event waiting, boolean isResting) { //toWait()
        super(id, qMax, isServing, nextTime, updatedQueue, waiting, isResting);
    }

    public SelfCheck(int id, int qMax, boolean isServing,
            double nextTime, PriorityQueue<Event> updatedQueue, boolean isResting) { //for resting
        super(id,qMax,isServing, nextTime, updatedQueue, isResting);
    }

    @Override
    public boolean canServe(Event cus) {
        if (isServing) {
            return false;
        } 
        return cus.getTime() >= nextTime;
    }

    public boolean canHold() {
        return isServing && selfCheckLine.size() < selfQMax;
    }

    public SelfCheck serve(Event cus, double serviceTime) {
        return new SelfCheck(this.id, this.qMax, true, cus.getTime() + serviceTime, 
                selfCheckLine, false);
    }

    public SelfCheck hold(Event cus) {
        selfCheckLine.add(cus);
        return new SelfCheck(this.id, this.qMax, true, this.nextTime, selfCheckLine, false);
    }

    @Override
    public SelfCheck serveWait(double serviceTime) {
        Event waitingCus = selfCheckLine.poll();
        return new SelfCheck(this.id, this.qMax, true, this.nextTime + serviceTime,
                selfCheckLine, waitingCus, false);
    }

    public SelfCheck toDone(double doneTime) {
        return new SelfCheck(this.id, this.qMax, false, doneTime, selfCheckLine, false);
    }

    
    public SelfCheck rest() throws Exception{
        throw new IllegalAccessException("Self-Checkout cannot rest");
    }


}
