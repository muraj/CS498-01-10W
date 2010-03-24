package team276;

import battlecode.common.*;

public class SoldierBot extends Bot {
    public SoldierBot(RobotController rc) throws Exception {
        super(rc);

        this.LOW_HP_THRESH = 30;
    }

    public void AI() throws Exception {
        while (true) {
            status = rc.senseRobotInfo(self);
            senseNear();
            attack();
            handleMovement();
            rc.yield();
        }
    }
}
