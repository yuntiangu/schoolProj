package cs2030.simulator;

/**
 * SeverRest event is generated when the server decides to rest.
 */
public class ServerRest extends Event {
    public ServerRest(int id, double time) {
        super(id,time);
        this.status = REST;
    }
}
