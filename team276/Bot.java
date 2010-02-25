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
    protected Movement movement;

    public Bot(RobotController rc, Team t) {
        this.rc = rc;
        this.id = rc.getRobot().getID();
        this.team = t;
        this.currentLocation = rc.getLocation();
        this.currentDirection = rc.getDirection();
    }

    public abstract void AI() throws Exception;

    public void beginUpkeep() {
        currentLocation = rc.getLocation();
        currentDirection = rc.getDirection();
        Debugger.debug_print("Current status: At: " + currentLocation + " Facing: " + currentDirection.name());
    }

    public void endUpkeep() {
        Debugger.debug_printTotalBCUsed();
        Debugger.debug_printEnergon(rc);
    }

    public void yield() {
        endUpkeep();
        rc.yield();
    }

    public void bp() {
        Debugger.debug_print("Breakpoint hit");
        rc.breakpoint();
        yield();
    }
}
