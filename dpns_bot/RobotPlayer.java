package dpns_bot;

import battlecode.common.Clock;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RobotPlayer implements Runnable {
    private final RobotController rc;

    public RobotPlayer(RobotController rc) {
        this.rc = rc;
   }

    public void run() {
        Bot b = null;

        System.out.println("RB->RUN()");

        switch(rc.getRobotType()) {
            case ARCHON:
                b = new ArchonBot(rc);
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
                System.out.println("Robot Type not supported yet.");
            break;
        }

        b.run();
     }
}

class Bot {
    public RobotController rc;

    public void run() {
        System.out.println("Nothing to see here");
    }
}

class ArchonBot extends Bot {
    public ArchonBot(RobotController rc) {
        this.rc = rc;
    }
    
    public void run() {
        while(true) {
            System.out.println("Bytecodes used: " + Clock.getBytecodeNum());
            rc.yield();
        }
    }
}
