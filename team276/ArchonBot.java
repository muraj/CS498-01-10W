package team276;

import battlecode.common.*;

public class ArchonBot extends Bot {
    private static final int MINIMUM_ENERGY_TO_SPAWN = 70;
    private static final int MINIMUM_ENERGY_TO_TRANSFER = 45;
    private static final int UNITENERGY_TRANSFER = 10;
    private boolean didSpawn;
    private int lastSpawnRound;

    public ArchonBot(RobotController rc) throws Exception {
        super(rc);

        didSpawn = false;
        lastSpawnRound = 0;
    }

    public void AI() throws Exception {
        Beacon b = new Beacon(rc.senseRobotInfo(rc.getRobot()));
        b.send(rc);
        while (true) {
            status = rc.senseRobotInfo(self);
            senseNear();
            spawnUnit();
            transferEnergon();
            handleMovement();

/*
            if (rc.isMovementActive()) {	//While on movement cooldown, crunch on compute AI <- Multiplexing!
                processMsgs(1000);
                rc.setIndicatorString(0,"QUEUE: "+msgQueue.size());
                resetMsgQueue();	//Clear the local queue
                rc.yield();
                continue;
            }
            Direction dir = status.directionFacing;
            MapLocation loc = status.location;
            MapLocation ahead = loc.add(dir);
            if (rc.senseTerrainTile(ahead).getType() == TerrainTile.TerrainType.LAND) {
                Robot r = rc.senseGroundRobotAtLocation(ahead);
                if (r == null) {
                    if (status.energonLevel > RobotType.SOLDIER.spawnCost()+MINIMUM_ENERGY_TO_SPAWN) {
                        rc.spawn(RobotType.SOLDIER);
                        rc.yield();
                        continue;
                    }
                } else {
                    RobotInfo ri = rc.senseRobotInfo(r);
                    if (ri.team == status.team && ri.energonLevel < ri.type.maxEnergon()
                            && status.energonLevel > MINIMUM_ENERGY_TO_TRANSFER && !ri.type.isBuilding()) {
                        rc.transferUnitEnergon(UNITENERGY_TRANSFER,ahead,RobotLevel.ON_GROUND);
                        rc.yield();
                        continue;
                    }
                }
            }
*/

            rc.yield();
        }
    }

    public void transferEnergon() throws Exception {
        Robot r;
        RobotInfo ri;
        MapLocation ahead;

        // Give buckets of energon to our new unit
        if(didSpawn) {
            ahead = status.location.add(status.directionFacing);
            r = rc.senseGroundRobotAtLocation(ahead);

            if(r == null) {
                if(Clock.getRoundNum() - lastSpawnRound == 0)
                    return;

                didSpawn = false;
                lastSpawnRound = 0;
                return;
            }

            ri = rc.senseRobotInfo(r);

            // Redundant checks if we just spawned, some maybe not necessary
            if(ri.team == status.team && ri.energonLevel <  ri.maxEnergon
                && status.energonLevel > MINIMUM_ENERGY_TO_TRANSFER && !ri.type.isBuilding()) {
                rc.transferUnitEnergon(UNITENERGY_TRANSFER, ahead, RobotLevel.ON_GROUND);
            }
        }

        // Give the peasents around us some energon
        for(int i = 0; i < nNeedEnergon; i++) {
            rc.transferUnitEnergon(1, alliedGround[needEnergon[i]].location, RobotLevel.ON_GROUND);
        }
    }

    public boolean canSpawn() throws Exception {
        MapLocation ahead;
        Robot r;

        if(rc.hasActionSet())
            return false;

        if(didSpawn)
            return false;

        if(status.energonLevel < MINIMUM_ENERGY_TO_SPAWN)
            return false;

        ahead = status.location.add(status.directionFacing);
        if(rc.senseTerrainTile(ahead).getType() != TerrainTile.TerrainType.LAND)
            return false;

        r = rc.senseGroundRobotAtLocation(ahead);
        if(r != null)
            return false;

        return true;
    }

    public void spawnUnit() throws Exception {
        if(!canSpawn())
            return;

        // TODO Spawn a unit based on numbers of needed units
        // if(needToSpawn()) {
            rc.spawn(RobotType.SOLDIER);
            didSpawn = true;
            lastSpawnRound = Clock.getRoundNum();
        // }
    }

    public void handleMovement() throws Exception {
        if(didSpawn || status.roundsUntilMovementIdle != 0 || rc.hasActionSet())
            return;

        // We need a better way to "guide" our archons for movement
        if(rc.canMove(status.directionFacing))
            rc.moveForward();
        else
            rc.setDirection(status.directionFacing.rotateRight());
    }
}
