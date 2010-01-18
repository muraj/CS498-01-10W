package team276;

import battlecode.common.RobotController;

public abstract class Bot {
    protected RobotController rc;
    protected int bcCounterStart;
    public Bot(RobotController rc) {
        this.rc = rc;
    }

    public abstract void AI() throws Exception;

    public void yield(){
        rc.yield();
    }
}
