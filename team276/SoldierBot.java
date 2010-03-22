package team276;

import battlecode.common.*;

public class SoldierBot extends Bot {
    public SoldierBot(RobotController rc) throws Exception {
        super(rc);
    }

    public void AI() throws Exception {
        while (true) {
            status = rc.senseRobotInfo(self);
            senseNear();

            if (status.roundsUntilMovementIdle != 0 || status.roundsUntilAttackIdle != 0) {	//Do heavy stuff while we're moving
                //Beacon b = new Beacon(rc.senseRobotInfo(rc.getRobot()));
                //b.send(rc);
                rc.yield();
                continue;
            }
            Direction f = flock(1, 1, 1, 1, 1);	//Play with these values.
            f = f == Direction.OMNI ? status.directionFacing : f;	//If OMNI, then AI failed, keep moving forward
            if (!rc.canMove(f)) {
                if (rc.canMove(f.rotateLeft())) f=f.rotateLeft();
                else f=f.rotateRight();
            }
            rc.setIndicatorString(2,"Flocking: "+f);
            Direction mydir = status.directionFacing;
            if (f != mydir && f != mydir.opposite()) { //Don't waste a turn turning if you don't have to.
                rc.setDirection(f);
                rc.yield();
            }		//Gaurenteed to either be facing f or opposite f here
            if (rc.canMove(f)) {
                // this rc.getDirection() currently left in for the moment since
                // this will happen immediately upon starting the next turn after
                // the preceding if.
                if (f==rc.getDirection()) rc.moveForward();
                else rc.moveBackward();	//Move backward if we're not facing that direction
            }
            rc.yield();
        }
    }
}
