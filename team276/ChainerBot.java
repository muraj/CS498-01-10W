package team276;

import battlecode.common.*;

public class ChainerBot extends Bot {
    public ChainerBot(RobotController rc, Team t) {
        super(rc,t);
    }

    public void AI() throws Exception{
        while(true){
            Debugger.debug_print("I'm a Chainer!");
            rc.yield();
        }
    }
}
