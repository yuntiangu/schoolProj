package cs2030.simulator;

import java.util.Scanner;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Event Simulator that simulates the sequence of events.
 * @param sc The scanner passed in from the Main (client) class.
 * @param servers The array of Servers.
 * @param events The PriorityQueue of concurrent Events, initlialised with an EventComparator.
 * @param allEvents The PriorityQueue of all recorded Events, in sequence with the EventComparator.
 * @param rng The Random Number Generator that generates probabilty and periods.
 * @param waitTime The total time spent waiting by all waiting Customers (Events).
 * @param k The integer value of the number of Servers.
 * @param nSelf The integer value of the number of Self-Checkout Counters.
 * @param serverRestingProb The probability that a Server rests after finishing a Customer (Event).
 * @param greedyProb The probability of a Customer generated being greedy.
 * @param arrivalTimeSupply The Supplier that supplies the stream of arrival timings.
 * @param serviceTimeSupply The Supplier that supplies the stream of service timings.
 * @param restProbSupply The Supplier that supplies the stream of resting probabilty.
 * @param restPeriodSupply The Supplier that supplies the stream of resting periods.
 * @param greedyProbSupply The Supplier that supplies the stream of greedy Customer probabiltiy.
 */
public class EventSimulator {
    private final Scanner sc;
    private final Comparator<Event> cmp = new EventComparator();
    private Server[] servers;
    private PriorityQueue<Event> events;
    private PriorityQueue<Event> allEvents;
    private RandomGenerator rng;
    private double waitTime = 0;
    private int k;
    private int nSelf;
    private double serverRestingProb;
    private double greedyProb;

    private Supplier<Double> arrivalTimeSupply;
    private Supplier<Double> serviceTimeSupply;
    private Supplier<Double> restProbSupply;
    private Supplier<Double> restPeriodSupply;
    private Supplier<Double> greedyProbSupply;
    
    /**
     * Constructor for EventSimulator.
     */ 
    public EventSimulator(Scanner scanner) {
        this.sc = scanner;
        events = new PriorityQueue<>(cmp);
        allEvents = new PriorityQueue<>(cmp);
    }

    /**
     * setUpShop to receive all inputs from user and initialise arrival Events and array of Servers.
     */
    public void setUpShop() {
        int seed = sc.nextInt(); //seed for rng;
        k = sc.nextInt(); //number of servers;
        nSelf = sc.nextInt();
        int queueMax = sc.nextInt();
        int numArrivals = sc.nextInt();
        double lamda = sc.nextDouble(); //arrival rate
        double mu = sc.nextDouble(); //service rate
        double rho = sc.nextDouble(); //resting rate
        serverRestingProb = sc.nextDouble(); //probability of resting
        greedyProb = sc.nextDouble();
        sc.close();

        rng = new RandomGenerator(seed, lamda, mu, rho); 

        arrivalTimeSupply = () -> rng.genInterArrivalTime();  
        serviceTimeSupply = () -> rng.genServiceTime();
        restProbSupply = () -> rng.genRandomRest();
        restPeriodSupply = () -> rng.genRestPeriod();
        greedyProbSupply = () -> rng.genCustomerType();

        servers = new Server[k + nSelf];
        for (int i = 0; i < k + nSelf; i++) {
            if (i < k) {
                servers[i] = new Server(i + 1, queueMax);
            } else {
                servers[i] = new SelfCheck(i + 1, queueMax);
            }
        }

        double arriveAt = 0.0;
        for (int i = 0; i < numArrivals; i++) { 
            boolean isGreedy = greedyOrNot(greedyProbSupply, greedyProb);
            events.add(new ArriveEvent(i + 1, arriveAt, isGreedy));
            arriveAt += arrivalTimeSupply.get(); 
        }
    }

    /**
     * simulate the sequence of events occurring.
     */
    public void simulate() throws Exception{
        while (events.size() > 0) {
            Event e = events.poll();
            allEvents.add(e);

            if (e instanceof ServerRest) { 
                int serverIdx = e.getId() - 1;
                events.add(new ServerBack(e.getId(), e.getTime() + restPeriodSupply.get()));
            } else if (e instanceof ServerBack) {
                int serverIdx = e.getId() - 1;
                servers[serverIdx] = servers[serverIdx].backFromRest(e.getTime());
                if (servers[serverIdx].stillGotWaiting()) {
                    serveWaiting(serverIdx, e);
                }
            } else if (e instanceof ArriveEvent) {
                Optional<Server> serverOptional = firstAvailableServer(servers, e);
                Optional<Server> waitServerOptional = firstServerToWait(servers, e);

                if (serverOptional.isPresent()) {
                    int i = serverOptional.get().getId() - 1;
                    servers[i] = servers[i].serve(e,serviceTimeSupply.get());
                    events.add(new ServedEvent(e.getId(), e.getTime(), i + 1, 
                                i >= k, e.isGreedy()));
                } else if (waitServerOptional.isPresent()) {
                    if (e.isGreedy()) {
                        int j = getShortestQueueServer(servers).getId() - 1;
                        servers[j] = servers[j].hold(e);
                        events.add(new WaitEvent(e.getId(), e.getTime(), j + 1, j >= k, 
                                    e.isGreedy()));
                    } else {
                        int j = waitServerOptional.get().getId() - 1;
                        servers[j] = servers[j].hold(e);
                        events.add(new WaitEvent(e.getId(), e.getTime(), j + 1, j >= k, 
                                    e.isGreedy()));
                    }
                } else {
                    allEvents.add(new LeaveEvent(e.getId(), e.getTime(), e.isGreedy()));
                }
            } else if (e instanceof ServedEvent) {
                int serverIdx = e.getServerId() - 1;
                double doneTime = servers[serverIdx].getNextServeTime();
                events.add(new DoneEvent(e.getId(), doneTime, e.getServerId(), serverIdx >= k, 
                            e.isGreedy()));
            } else if (e instanceof DoneEvent) {
                int serverIdx = e.getServerId() - 1;
                servers[serverIdx] = servers[serverIdx].toDone(e.getTime());
                if (isHumanServer(serverIdx)) { //is human server
                    double randomNumber = restProbSupply.get();
                    if (randomNumber < serverRestingProb) {
                        servers[serverIdx] = servers[serverIdx].rest();
                        events.add(new ServerRest(serverIdx + 1, e.getTime()));
                    } else {               
                        if (servers[serverIdx].stillGotWaiting()) {
                            serveWaiting(serverIdx, e);
                        } //end inner if
                    }
                } else { // is selfCheck
                    if (servers[serverIdx].stillGotWaiting()) {
                        serveWaiting(serverIdx, e);
                    }
                }
            } //end if DoneEvent
        } //end while
    } //end simulate

    public boolean greedyOrNot(Supplier<Double> greedyProbSupply, double greedyProb) {
        return greedyProbSupply.get() < greedyProb;
    }
    
    /**
     * Helper method to determine if server is a human server.
     */
    public boolean isHumanServer(int serverIdx) {
        return serverIdx < k;
    }

    /**
     * Helper method for each server to serve the first Customer in their queue.
     */
    public void serveWaiting(int serverIdx, Event e) {
        double startServeTime = servers[serverIdx].getNextServeTime();
        servers[serverIdx] = servers[serverIdx].serveWait(serviceTimeSupply.get());
        Event waiting = servers[serverIdx].getWaiting(); //the Customer(Event) waiting to be served.
        waitTime += startServeTime - waiting.getTime();
        events.add(new ServedEvent(waiting.getId(), e.getTime(), 
                    serverIdx + 1, serverIdx >= k, waiting.isGreedy()));
    }
    
    /**
     * Helper method for a greedy Cusotmer to choose the Server with the shortest queue.
     */
    public Server getShortestQueueServer(Server[] servers) {
        return Arrays.stream(servers)
            .sorted((x, y) -> x.queueLength() != y.queueLength() ? x.queueLength() - y.queueLength()
                    : x.getId() - y.getId())
            .findFirst()
            .get();
    }
    
    /**
     * Helper method for a Customer to find the first availble Server.
     */
    public Optional<Server> firstAvailableServer(Server[] servers, Event e) {
        return Arrays.stream(servers)
            .filter(x -> x.canServe(e))
            .findFirst();
    }
    
    /**
     * Helper method for a Customer to find the first Server who can hold this Customer.
     */
    public Optional<Server> firstServerToWait(Server[] servers, Event e) {
        return Arrays.stream(servers)
            .filter(x -> x.canHold())
            .findFirst();
    }

    /**
     * Method to print out all Events and the statistic associated with the sequence of Events.
     */
    public void getStats() {
        int numServed = 0;
        int numLeft = 0;
        while (allEvents.size() > 0) {
            Event pollE = allEvents.poll();
            if (pollE instanceof ServedEvent) {
                numServed++;
            }
            if (pollE instanceof LeaveEvent) {
                numLeft++;
            }
            if (pollE instanceof ServerRest || pollE instanceof ServerBack) {
                continue;
            }
            System.out.println(pollE);
        }
        double avgWaitTime = waitTime / numServed;
        if (Double.isNaN(avgWaitTime)) {
            avgWaitTime = 0.0;
        }
        System.out.println(String.format("[%.3f %d %d]",avgWaitTime,numServed,numLeft));
    }


}



