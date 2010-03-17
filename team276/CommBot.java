package team276;

import battlecode.common.*;

public class CommBot extends Bot {
    public CommBot(RobotController rc) throws Exception {
        super(rc);
    }

    public void AI() throws Exception {
        while (true) {
            //Debugger.debug_print("I'm a Comm!");
            rc.yield();
        }
    }
}
