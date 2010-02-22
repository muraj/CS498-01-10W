package team276;

import battlecode.common.*;

public class RobotPlayer implements Runnable {
    private Bot b;

    public RobotPlayer(RobotController rc) throws Exception {
        Team t = rc.getTeam();
        switch (rc.getRobotType()) {
        case ARCHON:
            b = new ArchonBot(rc,t);
            break;
        case AURA:
            b = new AuraBot(rc,t);
            break;
        case CHAINER:
            b = new ChainerBot(rc,t);
            break;
        case COMM:
            b = new CommBot(rc,t);
            break;
        case SOLDIER:
            b = new SoldierBot(rc,t);
            break;
        case TELEPORTER:
            b = new TeleBot(rc,t);
            break;
        case TURRET:
            b = new TurretBot(rc,t);
            break;
        case WOUT:
            b = new WoutBot(rc,t);
            break;
        default:
            throw new Exception("Robot Type not supported yet.");
        }
    }

    public void run() {
        try {
            b.AI();
        } catch (Exception e) {
            System.out.println("!! Caught Exception !!");
            e.printStackTrace();
        }
        Debugger.debug_printTotalBCUsed();
        b.yield();  //Should never get here.
    }
}
