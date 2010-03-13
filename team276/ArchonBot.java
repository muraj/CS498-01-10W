package team276;

import battlecode.common.*;

public class ArchonBot extends Bot {
    public ArchonBot(RobotController rc, Team t) {
        super(rc,t);
    }

    public void AI() throws Exception {
        Beacon b = new Beacon(rc.senseRobotInfo(rc.getRobot()));
        b.send(rc);
        while (true) {
            processMsgs();
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
