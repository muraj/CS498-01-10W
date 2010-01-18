package dpns_bot;

import battlecode.common.RobotController;

public class SoldierBot extends Bot {
    public SoldierBot(RobotController rc) {
        super(rc);
    }

    public void AI() {
        Debugger.debug_print("I'm a Soldier!");
    }
}
