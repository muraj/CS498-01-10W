package team276;

import battlecode.common.RobotController;

public class TurretBot extends Bot {
    public TurretBot(RobotController rc) {
        super(rc);
    }

    public void AI() {
        Debugger.debug_print("I'm a Turret!");
    }
}
