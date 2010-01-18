package team276;

import battlecode.common.*;

public class WoutBot extends Bot {
    public WoutBot(RobotController rc, Team t) {
        super(rc,t);
    }

    public void AI() throws Exception{
        while(true){
            Debugger.debug_print("I'm a Wout!");
            Debugger.debug_print_energon(this.rc);
            rc.yield();
        }
    }
}
