package team276;

import battlecode.common.*;

public class ArchonBot extends Bot {
    public ArchonBot(RobotController rc, Team t) {
        super(rc,t);
    }

    public void AI() throws Exception {
        Debugger.debug_Print("===Making beacon...===");
        Debugger.debug_SetCounter(this);
        Beacon b = new Beacon(rc.senseRobotInfo(rc.getRobot()));
        Debugger.debug_PrintBCUsed();
        Debugger.debug_PrintCounter(this);

        Debugger.debug_Print("===Sending beacon...===");
        Debugger.debug_SetCounter(this);
        b.send(rc);
        Debugger.debug_PrintBCUsed();
        Debugger.debug_PrintCounter(this);

        while (true) {
            Debugger.debug_Print("===Processing Messages...===");
            Debugger.debug_SetCounter(this);
            processMsgs();
            rc.setIndicatorString(0,"Queue: "+msgQueue.size());
            Debugger.debug_PrintBCUsed();
            Debugger.debug_PrintCounter(this);
            while(!msgQueue.isEmpty()){
                ParsedMsg m = msgQueue.poll();
                switch(m.type()){
                case BEACON:
                    Debugger.debug_Print(""+((Beacon)m).location());
                    break;
                }
            }
            rc.yield();
        }
    }
}
