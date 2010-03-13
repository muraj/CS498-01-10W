package team276;

import battlecode.common.*;
import java.util.PriorityQueue;

public abstract class Bot {
    protected final RobotController rc;
    protected final Team team;
    protected final int id;
    protected int bcCounterStart;
    protected MapLocation currentLocation;
    protected PriorityQueue<ParsedMsg> msgQueue;
    public Bot(RobotController rc, Team t) {
        this.rc = rc;
        this.id = rc.getRobot().getID();
        this.team = t;
        this.currentLocation = rc.getLocation();
        this.msgQueue = new PriorityQueue<ParsedMsg>(10, new Util.MessageComparator());
    }

    public abstract void AI() throws Exception;

    public void yield() {
        rc.yield();
    }

    public RobotController getRC() {
        return this.rc;
    }

    public final void processMsgs() throws Exception{
        for (Message m : rc.getAllMessages()) {
            if (m.ints == null || m.ints.length < 3)
                continue;
            switch (MSGTYPE.values()[m.ints[2]]) {
            case BEACON:
                msgQueue.add(new Beacon(m));
                break;
            case ATTACK:
                //msgQueue.add(new Beacon(m));
                break;
            }
        }
    }
}
