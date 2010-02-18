package SpawnPlayer;

import battlecode.common.RobotController;
import battlecode.common.Team;

public abstract class Bot {
    protected final RobotController rc;
    protected final Team team;
    protected final int id;
    protected int bcCounterStart;
    public Bot(RobotController rc, Team t) {
        this.rc = rc;
        this.id = rc.getRobot().getID();
        this.team = t;
    }

    public abstract void AI() throws Exception;

    public void yield() {
        rc.yield();
    }
}
