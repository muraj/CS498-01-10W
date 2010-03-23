package team276;

import battlecode.common.*;

public class ArchonBot extends Bot {
    private static final int MINIMUM_ENERGY_TO_SPAWN = 1;
    private static final int MINIMUM_ENERGY_TO_TRANSFER = 2;
    public ArchonBot(RobotController rc) throws Exception {
        super(rc);
    }

    public void AI() throws Exception {
        Beacon b = new Beacon(rc.senseRobotInfo(rc.getRobot()));
        b.send(rc);
        while (true) {
            if (rc.isMovementActive()) {	//While on movement cooldown, crunch on compute AI <- Multiplexing!
                processMsgs(1000);
                rc.setIndicatorString(0,"QUEUE: "+msgQueue.size());
                resetMsgQueue();	//Clear the local queue
                rc.yield();
                continue;
            }
            Direction dir = rc.getDirection();
            MapLocation loc = rc.getLocation();
            MapLocation ahead = loc.add(dir);
            if (rc.senseTerrainTile(ahead).getType() == TerrainTile.TerrainType.LAND) {
                Robot r = rc.senseGroundRobotAtLocation(ahead);
                if (r == null) {
                    if (rc.getEnergonLevel() > RobotType.SOLDIER.spawnCost()+RobotType.SOLDIER.maxEnergon()+1) {
                        rc.spawn(RobotType.SOLDIER);
                        rc.yield();
                        continue;
                    }
                } else {
                    RobotInfo ri = rc.senseRobotInfo(r);
                    if (ri.team == status.team && !ri.type.isBuilding()
                            && rc.getEnergonLevel() > 1 && ri.energonLevel+ri.eventualEnergon < ri.type.maxEnergon() + 10 //Account for reserve too
                            && ri.energonReserve < GameConstants.ENERGON_RESERVE_SIZE) {
                        rc.transferUnitEnergon(1, ahead, RobotLevel.ON_GROUND);
                        rc.yield();
                        continue;
                    }
                }
            }
            if (rc.canMove(rc.getDirection())) rc.moveForward();
            else rc.setDirection(dir.rotateRight());
            rc.yield();
        }
    }
}
