package team276;

import battlecode.common.*;

public class WoutBot extends Bot {
    public WoutBot(RobotController rc) throws Exception {
        super(rc);
    }
    public void AI() throws Exception {
        while (true) {
            //Debugger.debug_print("I'm a WOUT!");
            rc.yield();
        }
    }
}
