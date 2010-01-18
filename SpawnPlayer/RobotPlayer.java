package SpawnPlayer;

import battlecode.common.Clock;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RobotPlayer implements Runnable {
    private Bot b;

    public RobotPlayer(RobotController rc) throws Exception {
        switch(rc.getRobotType()) {
            case ARCHON:        b = new ArchonBot(rc);  break;
            case AURA:          b = new AuraBot(rc);    break;
            case CHAINER:       b = new ChainerBot(rc); break;
            case COMM:          b = new CommBot(rc);    break;
            case SOLDIER:       b = new SoldierBot(rc); break;
            case TELEPORTER:    b = new TeleBot(rc);    break;
            case TURRET:        b = new TurretBot(rc);  break;
            case WOUT:          b = new WoutBot(rc);    break;
            default:
                throw new Exception("Robot Type not supported yet.");
          }
    }

    public void run() {
        try {
            b.AI();
        } catch(Exception e) {
            System.out.println("!! Caught Exception !!");
            e.printStackTrace();
        }
        Debugger.debug_print_total_bc_used();
        b.yield();  //Should never get here.
    }
}