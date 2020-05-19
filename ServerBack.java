package cs2030.simulator;

/**
 * SeverBack event is generated when the server comes back from rest.
 */
public class ServerBack extends Event {
    public ServerBack(int id, double time) {
        super(id, time);
        this.status = BACK;
    }
}
