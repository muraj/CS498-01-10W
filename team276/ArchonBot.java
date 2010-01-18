package team276;

import battlecode.common.RobotController;

public class ArchonBot extends Bot {
    public ArchonBot(RobotController rc) {
        super(rc);
    }

    public void AI() {
        Debugger.debug_print("I'm an Archon!");
    }
}
