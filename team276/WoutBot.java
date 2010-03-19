package team276;

import battlecode.common.*;

public class WoutBot extends Bot {
    public enum WoutState {
        HARVEST, HEAL;
    }
    WoutState state;
    public WoutBot(RobotController rc) throws Exception {
        super(rc);
        state = WoutState.HARVEST;
    }

    public void AI() throws Exception {
        while (true) {
            if (rc.isMovementActive() || rc.isAttackActive()) {	//Do heavy stuff while we're moving
                //Beacon b = new Beacon(rc.senseRobotInfo(rc.getRobot()));
                //b.send(rc);
                rc.yield();
                continue;
            }
            final Direction mydir = rc.getDirection();
            final MapLocation myloc = rc.getLocation();
            for(MapLocation m : rc.senseAlliedArchons()){
                if(m.distanceSquaredTo(myloc)<=1)
                    rc.transferFlux(rc.getFlux(), m, RobotLevel.IN_AIR);
            }
            //Conditions for cases
            Direction f;
            switch(state){
            case HARVEST:
                f = flock(5,0,0,0,1);
            case HEAL:
            default:
            }
            f = f == Direction.OMNI ? mydir : f;	//If OMNI, then AI failed, keep moving forward
            if (!rc.canMove(f)) {
                if (rc.canMove(f.rotateLeft())) f=f.rotateLeft();
                else f=f.rotateRight();
            }
            rc.setIndicatorString(2,"Flocking: "+f);
            if (f != mydir && f != mydir.opposite()) { //Don't waste a turn turning if you don't have to.
                rc.setDirection(f);
                rc.yield();
            }		//Gaurenteed to either be facing f or opposite f here
            if (rc.canMove(f)) {
                if (f==rc.getDirection()) rc.moveForward();
                else rc.moveBackward();	//Move backward if we're not facing that direction
            }
            rc.yield();
        }
    }
}
