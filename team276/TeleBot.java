package team276;

import battlecode.common.*;

public class TeleBot extends Bot {
    public TeleBot(RobotController rc, Team t) {
        super(rc,t);
    }
    public void AI() throws Exception {
        while (true) {
            Debugger.debugPrint("I'm a Tele!");
            rc.yield();
        }
    }
}
