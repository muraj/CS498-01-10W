package team276;

import battlecode.common.*;

public class ArchonBot extends Bot {
    private static final int MINIMUM_ENERGY_TO_SPAWN = 1;
    private static final int MINIMUM_ENERGY_TO_TRANSFER = 2;
    private static final int UNITENERGY_TRANSFER = 1;
    public ArchonBot(RobotController rc, Team t) {
        super(rc,t);
    }

    public void AI() throws Exception {
        Beacon b = new Beacon(rc.senseRobotInfo(rc.getRobot()));
        b.send(rc);
        while (true) {
            if (rc.isMovementActive()){	//While on movement cooldown, crunch on compute AI <- Multiplexing!
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
                    if (rc.getEnergonLevel() > RobotType.WOUT.spawnCost()+MINIMUM_ENERGY_TO_SPAWN) {
                         rc.spawn(RobotType.WOUT);
                         rc.yield();
                         continue;
                    }
                } else {
                    RobotInfo ri = rc.senseRobotInfo(r);
                    if (ri.team == this.team && ri.energonLevel < ri.type.maxEnergon()
                            && rc.getEnergonLevel() > MINIMUM_ENERGY_TO_TRANSFER && !ri.type.isBuilding()) {
                        rc.transferUnitEnergon(UNITENERGY_TRANSFER,ahead,RobotLevel.ON_GROUND);
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
