package team276;

import battlecode.common.RobotController;
import battlecode.common.Team;

public abstract class Bot {
    protected RobotController rc;
    protected Team team;
    protected int bcCounterStart;
    public Bot(RobotController rc, Team t) {
        this.rc = rc;
        this.team = t;
    }

    public abstract void AI() throws Exception;

    public void yield(){
        rc.yield();
    }
}
