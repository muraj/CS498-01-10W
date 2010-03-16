package team276;

import battlecode.common.*;

public class AuraBot extends Bot {
    public AuraBot(RobotController rc) throws Exception {
        super(rc);
    }

    public void AI() throws Exception {
        while (true) {
            Debugger.debug_print("I'm an Aura!");
            rc.yield();
        }
    }
}
