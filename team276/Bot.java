package team276;

import battlecode.common.*;

public abstract class Bot {
    protected final RobotController rc;
    protected final Robot self;
    protected RobotInfo status;
    protected int bcCounterStart;

    public Bot(RobotController rc) throws Exception {
        this.rc = rc;
        this.self = rc.getRobot();
        this.status = rc.senseRobotInfo(self);
    }

    public abstract void AI() throws Exception;

    public void yield() {
        rc.yield();
    }

    public RobotController getRC() {
        return this.rc;
    }
}
