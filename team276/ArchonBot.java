package team276;

import battlecode.common.*;

public class ArchonBot extends Bot {
    public ArchonBot(RobotController rc) throws Exception {
        super(rc);
    }

    public void AI() throws Exception {
        while (true) {
            status = rc.senseRobotInfo(self);
            Debugger.debug_setCounter(this);
            Debugger.debug_print("I'm an Archon!");
            Debugger.debug_printEnergon(this.rc);
            Debugger.debug_printCounter(this);
            //Handle communication here.
            Debugger.debug_printBCUsed();
            rc.yield();
        }
    }
}
