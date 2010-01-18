package dpns_bot;

import battlecode.common.RobotController;

public class AuraBot extends Bot {
    public AuraBot(RobotController rc) {
        super(rc);
    }

    public void AI() {
        Debugger.debug_print("I'm an Aura!");
    }
}
