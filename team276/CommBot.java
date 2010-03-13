package team276;

import battlecode.common.*;

public class CommBot extends Bot {
    public CommBot(RobotController rc, Team t) {
        super(rc, t);
    }

    public void AI() throws Exception {
        while (true) {
            Debugger.debug_Print("I'm a Comm!");
            rc.yield();
        }
    }
}
