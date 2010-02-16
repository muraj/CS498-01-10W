package team276;

import battlecode.common.*;

public abstract class Bot {
    protected final RobotController rc;
    protected final Team team;
    protected final int id;
    protected int bcCounterStart;
    protected MapLocation currentLoc;
    protected boolean isLeader;
    protected int following;

    public Bot(RobotController rc, Team t) {
        this.rc = rc;
        this.id = rc.getRobot().getID();
        this.team = t;
        this.isLeader = false;
        this.currentLoc = rc.getLocation();
    }

    public abstract void AI() throws Exception;

    public void yield(){
        rc.yield();
    }
}
