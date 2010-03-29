package team276;

import battlecode.common.*;

public class SoldierBot extends Bot {
    public SoldierBot(RobotController rc) throws Exception {
        super(rc);

        this.LOW_HP_THRESH = 40;
    }

    public void AI() throws Exception {
        while (status.energonLevel < status.maxEnergon && status.energonReserve - GameConstants.ENERGON_RESERVE_SIZE > .5) {
            status = rc.senseRobotInfo(self);
            rc.yield();
        }

        while (true) {
            status = rc.senseRobotInfo(self);
            senseNear();
            processMsgs(1000);
            recvHighPriorityEnemy();
            sendHighPriorityEnemy();
            transferEnergon();
            if (!attack()){
                handleMovement();
            }
            resetMsgQueue();
            rc.yield();
        }
    }
}
