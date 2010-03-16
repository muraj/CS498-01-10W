package team276;

import battlecode.common.*;

public class WoutBot extends Bot {
    public WoutBot(RobotController rc, Team t) {
        super(rc,t);
    }

    public void AI() throws Exception {
        while (true) {
            while (rc.isMovementActive() || rc.isAttackActive()){
                Beacon b = new Beacon(rc.senseRobotInfo(rc.getRobot()));
                b.send(rc);
                rc.yield();
            }
            Direction f = flock(1, 1, 1, 1, 1);
            f = f == Direction.OMNI ? rc.getDirection() : f;
            if (!rc.canMove(f)) {
                if (rc.canMove(f.rotateLeft())) f=f.rotateLeft();
                else f=f.rotateRight();
            }
            rc.setIndicatorString(1,f.toString());
            Direction mydir = rc.getDirection();
            if (f != mydir && f != mydir.opposite()) { //Don't waste a turn turning if you don't have to.
                rc.setDirection(f);
                rc.yield();
            }
            if (rc.canMove(f)){
                if(f==rc.getDirection()) rc.moveForward();
                else rc.moveBackward();
            }
            rc.yield();
        }
    }
}
