package team276;

import battlecode.common.*;

public class TurretBot extends Bot {
    public TurretBot(RobotController rc, Team t) {
        super(rc,t);
    }

    public void AI() throws Exception {
        while (true) {
            beginUpkeep();
            Debugger.debug_print("I'm a Turret!");

            yield();
        }
    }
}
