package team276;

import battlecode.common.*;
import java.util.PriorityQueue;

public abstract class Bot {
    protected final RobotController rc;
    protected final Team team;
    protected final int id;
    protected int bcCounterStart;
    protected PriorityQueue<ParsedMsg> msgQueue;
    public Bot(RobotController rc, Team t) {
        this.rc = rc;
        this.id = rc.getRobot().getID();
        this.team = t;
        this.msgQueue = new PriorityQueue<ParsedMsg>(10, new Util.MessageComparator());
    }

    public abstract void AI() throws Exception;

    public void yield() {
        rc.yield();
    }

    public RobotController getRC() {
        return this.rc;
    }
    private static final int MAX = 1000;
    public final void processMsgs() throws Exception{
        int start = Clock.getBytecodeNum();
        Message m;
        while((m = rc.getNextMessage()) != null){
            if (m.ints == null || m.ints.length < 3)
                continue;
            Debugger.debug_Print("chksumming");
            Debugger.debug_PrintBCUsed();
            if (m.ints[0] != ParsedMsg.chksum(m)) continue;
            Debugger.debug_PrintBCUsed();

            switch (MSGTYPE.values()[m.ints[2]]) {
            case BEACON:
                msgQueue.add(new Beacon(m));
                break;
            case ATTACK:
                //msgQueue.add(new Attack(m));
                break;
            }
        }
    }
}
