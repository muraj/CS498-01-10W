package dpns_bot;

import battlecode.common.Clock;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RobotPlayer implements Runnable {
    private final RC rc;

    public RobotPlayer(RC rc) {
        this.rc = rc;
    }

    public void run() {

    }
}

abstract class RC implements RobotController {

}
