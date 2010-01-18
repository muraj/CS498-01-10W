package dpns_bot;

import battlecode.common.RobotController;

public class TeleBot extends Bot {
    public TeleBot(RobotController rc) {
        super(rc);
    }

    public void AI() {
        Debugger.debug_print("I'm a Tele!");
    }
}
