package team276;

import battlecode.common.*;
import java.util.Comparator;
import java.lang.Object;

public class Util {
    public final static MapLocation ZERO = new MapLocation(0,0);
    public static final int ROBOTINFO_SZ = 14;
    public static final Direction coordToDirection(int dx, int dy) {
        return ZERO.directionTo(new MapLocation(dx, dy));
    }
    /* Message Utility Functions */
    public static class MessageComparator implements Comparator<ParsedMsg> {
        public int compare(ParsedMsg m1, ParsedMsg m2) {
            // retval < 0 => m1 is bigger, retval == 0 => equal
            int x = m1.type().value - m2.type().value;
            if (x!=0) return x;	//Prioritize on type first
            x = m1.age() - m2.age();
            if (x!=0) return x;	//Then on Time-to-Live
            return m1.getNumBytes() - m2.getNumBytes();	//Last but not least, by size
        }
    }
    public static final double intstodbl(int x, int y) {
        return Double.doubleToRawLongBits(((long)x << 32) | (long)y);
    }
    //** WARNING - EVENTUAL ENERGON AND TEAM IS ARTIFICAL HERE!! **//
    public static final RobotInfo unserializeRobotInfo(int[] x, int s) {  //Expensive call!
        RobotType type = RobotType.values()[x[s+13]];
        return new RobotInfo(x[s+7],        //OMG params...
                             type,
                             Team.NEUTRAL,
                             new MapLocation(x[s+8],x[s+9]),
                             intstodbl(x[s+3],x[s+4]),
                             -1.0,
                             type.maxEnergon(),
                             x[s+10],
                             x[s+11],
                             Direction.values()[x[s+2]],
                             intstodbl(x[s+5],x[s+6]),
                             x[s+1]==1,
                             x[s+13]==1,
                             x[s+0]>0 ? AuraType.values()[x[s+0]] : null);
    }
    public static final void serializeRobotInfo(RobotInfo ri, int[] ret, int s) {
        ret[s+0] = ri.aura==null ? -1 : ri.aura.ordinal();
        ret[s+1] = ri.deployed ? 1 : 0;
        ret[s+2] = ri.directionFacing.ordinal();
        long tmp = Double.doubleToRawLongBits(ri.energonLevel);
        ret[s+3] = (int)(tmp >> 32);
        ret[s+4] = (int)(tmp & 0xFFFFFFFF);
        tmp = Double.doubleToRawLongBits(ri.flux);
        ret[s+5] = (int)(tmp >> 32);
        ret[s+6] = (int)(tmp & 0xFFFFFFFF);
        ret[s+7] = ri.id;
        ret[s+8] = ri.location.getX();
        ret[s+9] = ri.location.getY();
        ret[s+10] = ri.roundsUntilAttackIdle;
        ret[s+11] = ri.roundsUntilMovementIdle;
        ret[s+12] = ri.teleporting ? 1 : 0;
        ret[s+13] = ri.type.ordinal();
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
    public static final int iRND = iCHKSUM+1;
    public static final int iTYPE = iCHKSUM+2;
    public ParsedMsg(Message pm) throws Exception {
        //if (chksum(pm) != pm.ints[0])
        //    throw new Exception("CHKSUM not valid");
        this.m = pm; //May need to do a deep copy, not sure.
    }
    public ParsedMsg(int sz, int type) {
        m = new Message();
        m.ints = new int[sz];
        m.ints[1] = Clock.getRoundNum();
        m.ints[2] = type;
    }
    public final void send(RobotController rc) throws Exception {
        m.ints[0] = chksum(m);  //Compute chksum
        rc.broadcast(m);        //Broadcast it out.
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
    public final int age() {
        return Clock.getRoundNum() - m.ints[iRND];
    }
}
class Beacon extends ParsedMsg {
    public static final int INT_SZ = Util.ROBOTINFO_SZ + 3;
    public static final int ROBOT_INFO_START = 3;
    public static RobotInfo robotInfo;  //Cache the RobotInfo object on first use
    public Beacon(Message pm) throws Exception {
        super(pm);
    }
    public Beacon(RobotInfo ri) {
        super(INT_SZ, MSGTYPE.BEACON.value);
        Util.serializeRobotInfo(ri, m.ints, ROBOT_INFO_START);
    }
    public MSGTYPE type() {
        return MSGTYPE.BEACON;
    }
    public RobotInfo info() {
        if (robotInfo==null) //Cache!
            robotInfo = Util.unserializeRobotInfo(m.ints, ROBOT_INFO_START);
        return robotInfo;
    }
}
class Attack extends ParsedMsg {
    public static final int INT_SZ = Util.ROBOTINFO_SZ + 3;
    public static final int ROBOT_INFO_START = 3;
    public static RobotInfo robotInfo;  //Cache the RobotInfo object on first use
    public Attack(Message pm) throws Exception {
        super(pm);
    }
    public Attack(RobotInfo ri) {
        super(INT_SZ, MSGTYPE.ATTACK.value);
        Util.serializeRobotInfo(ri, m.ints, ROBOT_INFO_START);
    }
    public MSGTYPE type() {
        return MSGTYPE.ATTACK;
    }
    public RobotInfo info() {
        if (robotInfo==null) //Cache!
            robotInfo = Util.unserializeRobotInfo(m.ints, ROBOT_INFO_START);
        return robotInfo;
    }
}
