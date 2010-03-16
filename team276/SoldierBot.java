package team276;

import battlecode.common.*;

public class SoldierBot extends Bot {
    public SoldierBot(RobotController rc) throws Exception {
        super(rc);
    }

    public void AI() throws Exception {
        while (true) {
            Debugger.debug_print("I'm a Soldier!");
            rc.yield();
        }
    }
}
