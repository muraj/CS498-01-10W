package team276;

import battlecode.common.RobotController;

public class CommBot extends Bot {
    public CommBot(RobotController rc) {
        super(rc);
    }

    public void AI() throws Exception{
        while(true){
            Debugger.debug_print("I'm a Comm!");
            rc.yield();
        }
    }
}
