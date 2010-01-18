package SpawnPlayer;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class RobotPlayer implements Runnable {

    private final RobotController myRC;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }

    public void run() {
        while (true) {
            try {
                /*** beginning of main loop ***/
                while (myRC.isMovementActive()) {
                    myRC.yield();
                }
				if(myRC.getRobotType() == RobotType.ARCHON){
					MapLocation ahead = myRC.getLocation().add(myRC.getDirection());
					if(myRC.getLocation().isAdjacentTo(ahead) && myRC.senseTerrainTile(ahead).getType() == TerrainTile.TerrainType.LAND){
						Robot forward = myRC.senseGroundRobotAtLocation(ahead);
						if (forward == null && myRC.getEnergonLevel() > RobotType.WOUT.spawnCost()+1){
							myRC.spawn(RobotType.WOUT);
							myRC.yield();
						} else if(forward != null){
							RobotInfo fi = myRC.senseRobotInfo(forward);
							if ( fi.energonLevel < fi.type.maxEnergon() && myRC.getEnergonLevel() > 2){
								myRC.transferUnitEnergon(1, ahead, RobotLevel.ON_GROUND);
								myRC.yield();
							}
						}
					}
				}
                if (myRC.canMove(myRC.getDirection())) {
                    System.out.println("about to move");
                    myRC.moveForward();
                } else {
                    myRC.setDirection(myRC.getDirection().rotateRight());
                }
                myRC.yield();

                /*** end of main loop ***/
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }
}
