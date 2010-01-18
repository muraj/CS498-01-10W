package team276;

import battlecode.common.*;

public class ArchonBot extends Bot {
    public ArchonBot(RobotController rc, Team t) {
        super(rc,t);
    }

    public void AI() throws Exception{
        while(true){
            Debugger.debug_set_counter(this);
            Debugger.debug_print("I'm an Archon!");
            Debugger.debug_print_energon(this.rc);
            Debugger.debug_print_counter(this);
            //Handle communication here.
            Debugger.debug_print_total_bc_used();
            rc.yield();
        }
    }
}
