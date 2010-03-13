package team276;

import battlecode.common.*;

public class ChainerBot extends Bot {
    public ChainerBot(RobotController rc, Team t) {
        super(rc,t);
    }

    public void AI() throws Exception {
        while (true) {
            while (rc.isMovementActive() || rc.isAttackActive()) rc.yield();
            Direction f = flock(1,1,1,1,1);
            f = f == Direction.OMNI ? rc.getDirection() : f;
            if (!rc.canMove(f)) {
                if (rc.canMove(f.rotateLeft())) f=f.rotateLeft();
                else f=f.rotateRight();
            }
            rc.setIndicatorString(0,f.toString());
            if (f != rc.getDirection()) {
                rc.setDirection(f);
                rc.yield();
            }
            if (rc.canMove(rc.getDirection()))
                rc.moveForward();
            rc.yield();
        }
    }

}
