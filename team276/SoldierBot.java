package team276;

import battlecode.common.*;

public class SoldierBot extends Bot {
    public SoldierBot(RobotController rc, Team t) {
        super(rc, t);
    }

    public void AI() throws Exception {
        while (true) {
            //Debugger.debug_print("I'm a Soldier!");
            while (rc.isMovementActive() || rc.isAttackActive()) rc.yield();
            Robot[] sensed = rc.senseNearbyGroundRobots();
            if (rc.getRoundsUntilAttackIdle() == 0) {
                for (int i=0; i<sensed.length; i++) {
                    RobotInfo si = rc.senseRobotInfo(sensed[i]);
                    if (si.team == this.team) continue; //My friend
                    if (!rc.canAttackSquare(si.location)) continue; //Outside attack range
                    rc.attackGround(si.location);
                    rc.yield();
                    break;
                }
                if (rc.isAttackActive()) continue;
            }
            if (rc.canMove(rc.getDirection())) {
                rc.moveForward();
            } else {
                rc.setDirection(rc.getDirection().rotateRight());
            }
            rc.yield();
        }
    }
}
