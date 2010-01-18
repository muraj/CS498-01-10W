package team276;

import battlecode.common.RobotController;

public class ChainerBot extends Bot {
    public ChainerBot(RobotController rc) {
        super(rc);
    }

    public void AI() throws Exception{
        while(true){
            Debugger.debug_print("I'm a Chainer!");
            rc.yield();
        }
    }
}
