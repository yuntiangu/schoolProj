import cs2030.simulator.Event;
import cs2030.simulator.ArriveEvent;
import cs2030.simulator.LeaveEvent;
import cs2030.simulator.ServedEvent;
import cs2030.simulator.DoneEvent;
import cs2030.simulator.WaitEvent;
import cs2030.simulator.Server;
import cs2030.simulator.SelfCheck;
import cs2030.simulator.ServerRest;
import cs2030.simulator.ServerBack;
import cs2030.simulator.EventComparator;
import cs2030.simulator.EventSimulator;

import java.util.Scanner;

/**
 * Client class to initalise the simulation of events.
 */
public class Main {
    
    /**
     * Main method of the client class.
     */
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        EventSimulator simulator = new EventSimulator(sc);
        simulator.setUpShop();
        simulator.simulate();
        simulator.getStats();
    }
}
