package team000;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;
import battlecode.common.Clock;

public class RobotPlayer implements Runnable {

    private final RobotController myRC;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }

    public void run() {
        //System.out.println("STARTING");
        while (true) {
            try {

                switch(myRC.getRobotType()) {
                    case ARCHON:
                        System.out.println("BYECODEZ: " + Clock.getBytecodeNum());
                    break;

                    case AURA:
                        System.out.println("BasdfadsYECODEZ: " + Clock.getBytecodeNum());
                    break;

                    case CHAINER:
                        System.out.println("BYECODasasEZ: " + Clock.getBytecodeNum());
                    break;

                    case COMM: 
                        System.out.println("BYECODadsfEZ: " + Clock.getBytecodeNum());
                    break;

                    case SOLDIER:
                        System.out.println("BYECODadsfEZ: " + Clock.getBytecodeNum());
                    break;

                    case TELEPORTER: 
                        System.out.println("BYECODEadsfaZ: " + Clock.getBytecodeNum());
                    break;

                    case TURRET:
                        System.out.println("BYECODEasdfZ: " + Clock.getBytecodeNum());
                    break;

                    case WOUT:
                        System.out.println("BalkjkklkjYECODEZ: " + Clock.getBytecodeNum());
                    break;

                    default:
                        System.out.println("ARRRRRRRRR");
                    break;
                }

                /*** beginning of main loop ***/
                while (myRC.isMovementActive()) {
                    myRC.yield();
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
