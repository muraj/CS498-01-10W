package team276;

import battlecode.common.*;

public class TeleBot extends Bot {
    public TeleBot(RobotController rc) throws Exception {
        super(rc);
    }

    public void AI() throws Exception {
        while (true) {
            //Debugger.debug_print("I'm a Tele!");
            rc.yield();
        }
    }
}
