package cs2030.simulator;

import java.util.Comparator;

/**
 * Event Comparator that ensures that the events in queue are in order of priority.
 */
public class EventComparator implements Comparator<Event> {

    @Override
    public int compare(Event a, Event b) {
        if (Double.compare(a.getTime(), b.getTime()) != 0) {
            return Double.compare(a.time, b.time);
        } else if (a.id == b.id) {
            return a.status - b.status;
        } else if (Double.compare(a.time, b.time) == 0 && a.id != b.id) {
            return a.id - b.id;
        }
        return -Integer.compare(a.getStatus(), b.getStatus());
    }
}


