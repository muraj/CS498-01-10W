package team276;

import battlecode.common.*;

public class ChainerBot extends Bot {
    public ChainerBot(RobotController rc) throws Exception {
        super(rc);

        this.LOW_HP_THRESH = 50;
    }

    public void AI() throws Exception {
    	int wakeUp = Clock.getRoundNum() + status.type.wakeDelay();
    	while(Clock.getRoundNum() <= wakeUp)
    		rc.yield();

        while (true) {
            status = rc.senseRobotInfo(self);
            senseNear();
            recvHighPriorityEnemy();
            transferEnergon();
            if(!attack())
            	handleMovement();
            //rc.yield();
            yield();
        }
    }
}
