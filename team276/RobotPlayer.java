package team276;

import battlecode.common.*;

public class RobotPlayer implements Runnable {
    private Bot b;

    public RobotPlayer(RobotController rc) throws Exception {
        createBot(rc);
    }
    public static final boolean DEBUG = true;
    public void run() {
        while (true) {	//Just a safety net in case we screw up.
            try {
                b.AI();
            } catch (Exception e) {		//We fucked up.
                System.out.println("!! Caught Exception !!");
                e.printStackTrace();
                if (DEBUG) {
                    b.getRC().breakpoint();    //Debugging, breakpoint, then kill the bot
                    break;
                } else {
                    try {
                        createBot(b.getRC());	//If we're not debugging, 'save' the bot by resetting it.
                    } catch (Exception e2) {
                        break;
                    }
                }
            }
        }
    }
    private void createBot(RobotController rc) throws Exception {
        switch (rc.getRobotType()) {
        case ARCHON:
            b = new ArchonBot(rc);
            break;
        case AURA:
            b = new AuraBot(rc);
            break;
        case CHAINER:
            b = new ChainerBot(rc);
            break;
        case COMM:
            b = new CommBot(rc);
            break;
        case SOLDIER:
            b = new SoldierBot(rc);
            break;
        case TELEPORTER:
            b = new TeleBot(rc);
            break;
        case TURRET:
            b = new TurretBot(rc);
            break;
        case WOUT:
            b = new WoutBot(rc);
            break;
        default:
            throw new Exception("Robot Type not supported yet.");
        }
    }
}
