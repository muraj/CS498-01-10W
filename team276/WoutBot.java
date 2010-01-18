package team276;

import battlecode.common.RobotController;

public class WoutBot extends Bot {
    public WoutBot(RobotController rc) {
        super(rc);
    }

    public void AI() throws Exception{
        while(true){
            Debugger.debug_print("I'm a Wout!");
            Debugger.debug_print_energon(this.rc);
            rc.yield();
        }
    }
}
