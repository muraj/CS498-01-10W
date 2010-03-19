package team276;

import battlecode.common.*;
import java.util.PriorityQueue;

public abstract class Bot {
    protected final RobotController rc;
    protected final Robot self;
    protected RobotInfo status;
    protected int bcCounterStart;
    protected PriorityQueue<ParsedMsg> msgQueue;
    protected final int MAX_GROUP_SZ = 3;
    public Bot(RobotController rc) throws Exception {
        this.rc = rc;
        this.self = rc.getRobot();
        this.status = rc.senseRobotInfo(self);
        this.msgQueue = new PriorityQueue<ParsedMsg>(10, new Util.MessageComparator());
    }
    public final void resetMsgQueue() {
        msgQueue.clear();   //Cheaper to make a new one?
    }
    public abstract void AI() throws Exception;

    public void yield() {
        rc.yield();
    }

    public RobotController getRC() {
        return rc;
    }
    public Direction flock(double SEPERATION, double COHESION, double ALIGNMENT, double COLLISION, double GOAL) throws Exception {
        int[] seperation=new int[2], align=new int[2], goal=new int[2], collision=new int[2];
        MapLocation myloc = rc.getLocation();
        /* General swarm */
        Robot[] rl = rc.senseNearbyGroundRobots();
        int c = 0;
        for (Robot r : rl) {
            if (c > MAX_GROUP_SZ) break;    //Try to limit bytecodes.
            RobotInfo ri = rc.senseRobotInfo(r);
            if (rc.getTeam() != ri.team) continue;
            else c++;
            seperation[0]+= myloc.getX() - ri.location.getX();
            seperation[1]+= myloc.getY() - ri.location.getY();
            align[0]+=ri.directionFacing.dx;
            align[1]+=ri.directionFacing.dy;
        }
        /* COLLISION GOAL */
        if (COLLISION != 0) {
            for (Direction d : Direction.values()) {
                if (d == Direction.OMNI || d == Direction.NONE) continue;
                TerrainTile t = rc.senseTerrainTile(myloc.add(d));
                if (t != null && t.getType() != TerrainTile.TerrainType.LAND) {
                    collision[0] -= d.dx;
                    collision[1] -= d.dy;
                }
            }
        }
        /* LEADER GOAL */
        if (GOAL != 0) {
            MapLocation leader = null;
            double glen = Double.MAX_VALUE;
            for (MapLocation t : rc.senseAlliedArchons()) {
                double tdist = myloc.distanceSquaredTo(t);
                if (tdist < glen) {
                    leader = t;
                    glen = tdist;
                }
            }
            if (leader != null && rc.canSenseSquare(leader)) {
                Direction leader_dir = rc.senseRobotInfo(rc.senseAirRobotAtLocation(leader)).directionFacing;
                goal[0] = leader.getX()+5*leader_dir.dx - myloc.getX();
                goal[1] = leader.getY()+5*leader_dir.dy - myloc.getY();
            } else if (leader != null) {
                goal[0] = leader.getX() - myloc.getX();
                goal[1] = leader.getY() - myloc.getY();
            }
        }
        /* Calculate Vector lengths */
        double slen = Util.ZERO.distanceSquaredTo(new MapLocation(seperation[0], seperation[1]));
        double alen = Util.ZERO.distanceSquaredTo(new MapLocation(align[0], align[1]));
        double clen = Util.ZERO.distanceSquaredTo(new MapLocation(collision[0], collision[1]));
        double glen = GOAL != 0 ? Math.sqrt(Util.ZERO.distanceSquaredTo(new MapLocation(goal[0], goal[1]))) : 1;

        slen = slen == 0 ? 1 : Math.sqrt(slen);    //Prevent divide by zero
        alen = alen == 0 ? 1 : Math.sqrt(alen);
        clen = clen == 0 ? 1 : Math.sqrt(clen);
        /* Sum the vectors */
        double outx = -seperation[0]/slen*(SEPERATION - COHESION)    //Cohesion == -Seperation
                      + align[0]*ALIGNMENT/alen
                      + collision[0]*COLLISION/clen
                      + goal[0]*GOAL/glen;
        double outy = -seperation[1]/slen*(SEPERATION - COHESION)
                      + align[1]*ALIGNMENT/alen
                      + collision[1]*COLLISION/clen
                      + goal[1]*GOAL/glen;
        return Util.coordToDirection((int)(outx*10), (int)(outy*10));
    }
    public final void processMsgs(int MAXBC) throws Exception {
        Message m;
        int startbc = Clock.getBytecodeNum();
        while ((m = rc.getNextMessage()) != null) {
            if (m.ints == null || m.ints.length < 3) continue;
            if (m.ints[0] != ParsedMsg.chksum(m)) continue;
            switch (MSGTYPE.values()[m.ints[2]]) {
            case BEACON:
                msgQueue.add(new Beacon(m));
                break;
            case ATTACK:
                //msgQueue.add(new Attack(m));
                break;
            }
            if (Clock.getBytecodeNum() - startbc >= MAXBC) break;
        }
        rc.getAllMessages();    //Clear global queue - may loose messages, but they'll be old anyway
    }
}
