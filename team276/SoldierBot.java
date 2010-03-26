package team276;

import battlecode.common.*;

public class SoldierBot extends Bot {
    public SoldierBot(RobotController rc) throws Exception {
        super(rc);

        this.LOW_HP_THRESH = 35;
    }

    public void AI() throws Exception {
        while(status.energonLevel - status.maxEnergon > 1 && status.energonReserve - GameConstants.ENERGON_RESERVE_SIZE > 1) {
            status = rc.senseRobotInfo(self);
            rc.yield();
        }

        while (true) {
            status = rc.senseRobotInfo(self);
            senseNear();
            recvHighPriorityEnemy();
            transferEnergon();
            attack();
            handleMovement();
            rc.yield();
        }
    }
}
