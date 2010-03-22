package team276;

import battlecode.common.*;
import java.util.Comparator;
import java.lang.Object;

public class Util {
    public final static MapLocation ZERO = new MapLocation(0,0);
    public static final Direction coordToDirection(int dx, int dy) {
        return ZERO.directionTo(new MapLocation(dx, dy));
    }
    /* Message Utility Functions */
    public static class MessageComparator implements Comparator<ParsedMsg> {
        public int compare(ParsedMsg m1, ParsedMsg m2) {
            // retval < 0 => m1 is bigger, retval == 0 => equal
            int x = m1.type().value - m2.type().value;
            if (x!=0) return x;	//Prioritize on type first
            x = m1.ttl() - m2.ttl();
            if (x!=0) return x;	//Then on Time-to-Live
            return m1.getNumBytes() - m2.getNumBytes();	//Last but not least, by size
        }
    }
}
enum MSGTYPE {
    BEACON(0), ATTACK(1), NONE(2);
    public final int value;
    MSGTYPE(int val) {
        value = val;
    }
}
class ParsedMsg extends Object {
    protected Message m;
    public static int INT_SZ = 3;
    public static final int INIT_TTL = 10;
    public static final int iCHKSUM = 0;
    public static final int iTTL = iCHKSUM+1;
    public static final int iTYPE = iCHKSUM+2;
    public ParsedMsg(Message pm) throws Exception {
        //if (chksum(pm) != pm.ints[0])
        //    throw new Exception("CHKSUM not valid");
        this.m = pm; //May need to do a deep copy, not sure.
    }
    public ParsedMsg(int sz, int type) {
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
    public static final int chksum(Message m) {
        return CHKSEED ^ m.getNumBytes();
    }
    public final int getNumBytes() {
        return m.getNumBytes();
    }
    public MSGTYPE type() {
        return MSGTYPE.NONE;
    }
    public final int ttl() {
        return m.ints[iTTL];
    }
}
class Beacon extends ParsedMsg {
    public static int INT_SZ = 24;
    public static final int LOCX = 15;
    public static final int LOCY = LOCX + 1;
    public static final int ROBOTYPE = 23;
    public Beacon(Message pm) throws Exception {
        super(pm);
    }
    public Beacon(RobotInfo ri) {
        super(INT_SZ, MSGTYPE.BEACON.value);
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
    public MSGTYPE type() {
        return MSGTYPE.BEACON;
    }
}
