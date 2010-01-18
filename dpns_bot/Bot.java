package dpns_bot;

import battlecode.common.RobotController;

public class Bot {
    protected RobotController rc;

    public Bot(RobotController rc) {
        this.rc = rc;
    }

    public void AI() {
        System.out.println("I'm a bot!");
    }

    public void yield(){
        rc.yield();
    }
}
