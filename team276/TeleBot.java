package team276;

import battlecode.common.RobotController;

public class TeleBot extends Bot {
    public TeleBot(RobotController rc) {
        super(rc);
    }

    public void AI() throws Exception{
        while(true){
            Debugger.debug_print("I'm a Tele!");
            rc.yield();
        }
    }
}