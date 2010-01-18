package dpns_bot;

import battlecode.common.RobotController;

public class ChainerBot extends Bot {
    public ChainerBot(RobotController rc) {
        super(rc);
    }

    public void AI() {
        Debugger.debug_print("I'm a Chainer!");
    }
}
