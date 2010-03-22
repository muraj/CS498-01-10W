package team276;

import battlecode.common.*;

public class TurretBot extends Bot {
    public TurretBot(RobotController rc) throws Exception {
        super(rc);
    }

    public void AI() throws Exception {
        while (true) {
            status = rc.senseRobotInfo(self);
            senseNear();

            //Debugger.debug_print("I'm a Turret!");
            rc.yield();
        }
    }
}
