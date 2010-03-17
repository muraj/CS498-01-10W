package team276;

import battlecode.common.*;

public class ChainerBot extends Bot {
    public ChainerBot(RobotController rc) throws Exception {
        super(rc);
    }

    public void AI() throws Exception {
        while (true) {
            if (rc.isMovementActive() || rc.isAttackActive()){	//Do heavy stuff while we're moving
                //Beacon b = new Beacon(rc.senseRobotInfo(rc.getRobot()));
                //b.send(rc);
                rc.yield();
				continue;
            }
            Direction f = flock(1, 1, 1, 1, 1);	//Play with these values.
            f = f == Direction.OMNI ? rc.getDirection() : f;	//If OMNI, then AI failed, keep moving forward
            if (!rc.canMove(f)) {
                if (rc.canMove(f.rotateLeft())) f=f.rotateLeft();
                else f=f.rotateRight();
            }
            rc.setIndicatorString(2,"Flocking: "+f);
            Direction mydir = rc.getDirection();
            if (f != mydir && f != mydir.opposite()) { //Don't waste a turn turning if you don't have to.
                rc.setDirection(f);
                rc.yield();
            }		//Gaurenteed to either be facing f or opposite f here
            if (rc.canMove(f)){
                if(f==rc.getDirection()) rc.moveForward();
                else rc.moveBackward();	//Move backward if we're not facing that direction
            }
            rc.yield();
        }
    }

}
