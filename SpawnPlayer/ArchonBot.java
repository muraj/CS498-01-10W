package SpawnPlayer;

import battlecode.common.*;

public class ArchonBot extends Bot {
    private static final int MINIMUM_ENERGY_TO_SPAWN = 1;
    private static final int MINIMUM_ENERGY_TO_TRANSFER = 2;
    private static final int UNITENERGY_TRANSFER = 1;
    public ArchonBot(RobotController rc, Team t) {
        super(rc,t);
    }

    public void AI() throws Exception{
        while(true){
            while(rc.isMovementActive()) rc.yield();
            Direction dir = rc.getDirection();
            MapLocation loc = rc.getLocation();
            MapLocation ahead = loc.add(dir);
            if(loc.isAdjacentTo(ahead) && rc.senseTerrainTile(ahead).getType() == TerrainTile.TerrainType.LAND){
                Robot r = rc.senseGroundRobotAtLocation(ahead);
                if(r == null){
                    if(rc.getEnergonLevel() > RobotType.SOLDIER.spawnCost()+MINIMUM_ENERGY_TO_SPAWN){
                        rc.spawn(RobotType.SOLDIER);
                        rc.yield();
                        continue;
                    }
                }
                else{
                    RobotInfo ri = rc.senseRobotInfo(r);
                    if(ri.team == this.team && ri.energonLevel < ri.type.maxEnergon()
                        && rc.getEnergonLevel() > MINIMUM_ENERGY_TO_TRANSFER && !ri.type.isBuilding()){
                        rc.transferUnitEnergon(UNITENERGY_TRANSFER,ahead,RobotLevel.ON_GROUND);
                        rc.yield();
                        continue;
                    }
                }
            }
            if(rc.canMove(rc.getDirection())) rc.moveForward();
            else rc.setDirection(dir.rotateRight());
            Debugger.debug_print_total_bc_used();
            rc.yield();
        }
    }
}