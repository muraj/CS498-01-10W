package dpns_bot;

import battlecode.common.RobotController;

public class CommBot extends Bot {
    public CommBot(RobotController rc) {
        super(rc);
    }

    public void AI() {
        Debugger.debug_print("I'm a Comm!");
    }
}
