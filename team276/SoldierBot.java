package team276;

import battlecode.common.*;

public class SoldierBot extends Bot {
    public SoldierBot(RobotController rc) throws Exception {
        super(rc);

        this.LOW_HP_THRESH = 10;
    }

    public void AI() throws Exception {
        while(status.energonLevel - status.maxEnergon > 1 && status.energonReserve - GameConstants.ENERGON_RESERVE_SIZE > 1) {
            status = rc.senseRobotInfo(self);
            rc.setIndicatorString(0, "Reserve: " + status.energonReserve);
            rc.yield();
        }

        while (true) {
            status = rc.senseRobotInfo(self);
            rc.setIndicatorString(0, "Reserve: " + status.energonReserve);
            senseNear();
            transferEnergon();
            attack();
            handleMovement();
            rc.yield();
        }
    }
}
