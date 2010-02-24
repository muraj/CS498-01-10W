package team276;

import battlecode.common.*;

public class SoldierBot extends Bot {
    public SoldierBot(RobotController rc, Team t) {
        super(rc, t);
        SEPERATION = 1.0;
        COHESION = 1.0;
        ALIGNMENT = 1.0;
        COLLISION = 1.0;
        GOAL = 1.0;
    }

    public void AI() throws Exception {
        while (true) {
            while (rc.isMovementActive() || rc.isAttackActive()) rc.yield();
            Direction f = flock();
            f = f == Direction.OMNI ? rc.getDirection() : f;
            if(!rc.canMove(f)){
                if (rc.canMove(f.rotateLeft())) f=f.rotateLeft();
                else f=f.rotateRight();
            }
            if(f != rc.getDirection()){
                rc.setDirection(f);
                rc.yield();
            }
            if(rc.canMove(rc.getDirection()))
                rc.moveForward();
            rc.yield();
        }
    }
}
