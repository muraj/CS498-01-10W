package team276;

import battlecode.common.*;
import java.util.Comparator;

public class Util {
    private final static MapLocation zero = new MapLocation(0,0);
    public static Direction coordToDirection(int dx, int dy) {
        return zero.directionTo(new MapLocation(dx, dy));
    }
    /* Message Utility Functions */
    public static class MessageComparator implements Comparator<Message> {
        public int compare(Message m1, Message m2) {
            // retval < 0 => m1 is bigger, retval == 0 => equal
            return m1.getNumBytes() - m2.getNumBytes();
        }
    }
}
class ParsedMsg {
    protected Message m;
    public static final int TYPE = 0;
    public static final int INIT_TTL = 10;
    public static final int INT_SZ = 3;
    public ParsedMsg(Message pm) throws Exception {
        if (chksum(pm) != pm.ints[0])
            throw new Exception("CHKSUM not valid");
        if (pm.ints[2] != TYPE)
            throw new Exception("Wrong Type");
        this.m = pm; //May need to do a deep copy, not sure.
    }
    public ParsedMsg(int sz, int type) {
        Debugger.debug_Print(""+sz);
        m = new Message();
        m.ints = new int[sz];
        m.ints[1] = INIT_TTL;
        m.ints[2] = type;
    }
    public final void send(RobotController rc) throws Exception {
        m.ints[0] = chksum(m);  //Compute chksum
        rc.broadcast(m);        //Broadcast it out.
    }
    protected static final double intstodbl(int x, int y) { //TODO
        return 0.0;
    }
    protected static final void dbltoints(double d, int i1, int i2) { //TODO
        return;
    }
    private static final int CHKSEED = 0x5B125AB;  //Some random starting value
    private static final int chksum(Message m) {
        int ret = CHKSEED;
        if (m.strings != null) {
            for (int i=0; i<m.strings.length; i++) {
                if (m.strings[i] == null) continue;
                for (int j=0; j<m.strings[i].length(); j++)
                    ret ^= m.strings[i].charAt(j); //Not a 'good' way to do this.
            }
        }
        if (m.ints != null) {
            for (int i=1; i<m.ints.length; i++) {   //Start at one to skip the initial chksum number
                ret ^= m.ints[i];
            }
        }
        return ret ^ m.getNumBytes();   //Not sure if needed.
    }
}
class Beacon extends ParsedMsg {
    public static final int TYPE = 1;
    public static final int INT_SZ = 24;
    public static final int LOCX = 15;
    public static final int LOCY = LOCX + 1;
    public static final int ROBOTYPE = 23;
    public Beacon(Message pm) throws Exception {
        super(pm);
    }
    public Beacon(RobotInfo ri) {
        super(INT_SZ, TYPE);
        MapLocation loc = ri.location;
        m.ints[LOCX] = loc.getX();
        m.ints[LOCY] = loc.getY();
        m.ints[ROBOTYPE] = ri.type.ordinal();
    }
    public final MapLocation location() {
        return new MapLocation(m.ints[LOCX], m.ints[LOCY]);
    }
    public final RobotType robottype() {
        return RobotType.values()[m.ints[ROBOTYPE]];
    }
}
