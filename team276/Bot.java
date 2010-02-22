package team276;

import battlecode.common.*;

public abstract class Bot {
    protected final RobotController rc;
    protected final Team team;
    protected final int id;
    protected int bcCounterStart;               // Count the BCs
    protected int bcRoundCounterStart;          // Count the rounds
    protected MapLocation currentLocation;
    protected Direction currentDirection;
    protected MapLocation movementTarget;       // The overall target for the current set of movements
    protected boolean underWay;                 // Are we currently moving towards a movementTarget?

    public Bot(RobotController rc, Team t) {
        this.rc = rc;
        this.id = rc.getRobot().getID();
        this.team = t;
        this.currentLocation = rc.getLocation();
        this.currentDirection = rc.getDirection();
        this.movementTarget = null;
        this.underWay = false;
    }

    public abstract void AI() throws Exception;

    public void beginUpkeep() {
        currentLocation = rc.getLocation();
        currentDirection = rc.getDirection();
    }

    public void endUpkeep() {
        Debugger.debug_printTotalBCUsed();
    }

    public void yield() {
        endUpkeep();
        rc.yield();
    }
}
