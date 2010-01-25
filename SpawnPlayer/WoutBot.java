package SpawnPlayer;

import battlecode.common.*;

public class WoutBot extends Bot {
    public WoutBot(RobotController rc, Team t) {
        super(rc,t);
    }

    public void AI() throws Exception{
        while(true){
            while(rc.isMovementActive() || rc.hasActionSet()) rc.yield();
            MapLocation myloc = rc.getLocation();
            for(MapLocation x : rc.senseAlliedArchons()){
                if(x.isAdjacentTo(myloc) || x.equals(myloc)){
                    rc.transferFlux(rc.getFlux(),x,RobotLevel.IN_AIR);
                    break;
                }
            }
            rc.yield();
        }
    }
}
