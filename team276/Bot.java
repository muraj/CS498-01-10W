package team276;

import battlecode.common.*;
import java.util.PriorityQueue;

public abstract class Bot {
    protected final RobotController rc;
    protected int bcCounterStart;
    protected Team team;
    protected int id;
    protected PriorityQueue<ParsedMsg> msgQueue;
    /* Swarming non-constant constants */
    protected final int MAX_GROUP_SZ = 3;
    public Bot(RobotController rc, Team t) {
        this.rc = rc;
        this.id = rc.getRobot().getID();
        this.team = t;
        this.msgQueue = new PriorityQueue<ParsedMsg>(10, new Util.MessageComparator());
    }
    public final void resetMsgQueue() {
        msgQueue = new PriorityQueue<ParsedMsg>(10, new Util.MessageComparator());
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
        Debugger.debug_print("Gathering rules");
        Debugger.debug_print_bc_used();
        Debugger.debug_set_counter(this);
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
        Debugger.debug_print_counter(this);
        /* COLLISION GOAL */
        Debugger.debug_print("Collision Rule");
        Debugger.debug_set_counter(this);
        for (Direction d : Direction.values()) {
            if (d == Direction.OMNI || d == Direction.NONE) continue;
            TerrainTile t = rc.senseTerrainTile(myloc.add(d));
            if (t != null && t.getType() != TerrainTile.TerrainType.LAND) {
                collision[0] -= d.dx;
                collision[1] -= d.dy;
            }
        }
        Debugger.debug_print_counter(this);
        Debugger.debug_print_bc_used();
        /* LEADER GOAL */
        MapLocation leader = null;
        double glen = Double.MAX_VALUE;
        Debugger.debug_print("Finding closest Archon");
        Debugger.debug_set_counter(this);
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
        Debugger.debug_print_bc_used();
        Debugger.debug_print_counter(this);
        /* Calculate Vector lengths */
        double slen = Util.ZERO.distanceSquaredTo(new MapLocation(seperation[0], seperation[1]));
        double alen = Util.ZERO.distanceSquaredTo(new MapLocation(align[0], align[1]));
        double clen = Util.ZERO.distanceSquaredTo(new MapLocation(collision[0], collision[1]));
        glen = Util.ZERO.distanceSquaredTo(new MapLocation(goal[0], goal[1]));

        slen = slen == 0 ? 1 : Math.sqrt(slen);    //Prevent divide by zero
        alen = alen == 0 ? 1 : Math.sqrt(alen);
        clen = clen == 0 ? 1 : Math.sqrt(clen);
        glen = glen == 0 ? 1 : Math.sqrt(glen);
        /* Sum the vectors */
        Debugger.debug_print("Applying rules");
        Debugger.debug_set_counter(this);
        double outx = -seperation[0]/slen*(SEPERATION - COHESION)    //Cohesion == -Seperation
                      + align[0]*ALIGNMENT/alen
                      + collision[0]*COLLISION/clen
                      + goal[0]*GOAL/glen;
        double outy = -seperation[1]/slen*(SEPERATION - COHESION)
                      + align[1]*ALIGNMENT/alen
                      + collision[1]*COLLISION/clen
                      + goal[1]*GOAL/glen;
        Debugger.debug_print_counter(this);
        return Util.coordToDirection((int)(outx*10), (int)(outy*10));
    }
    public final void processMsgs(int MAXBC) throws Exception{
        Message m;
        int startbc = Clock.getBytecodeNum();
        while((m = rc.getNextMessage()) != null){
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
            if (Clock.getBytecodeNum() - startbc >= MAXBC) {
                Debugger.debug_print("OMG WE HIT THE BC BARRIER");
                Debugger.debug_print(""+(Clock.getBytecodeNum() - startbc));
                break;
            }
        }
        //rc.getAllMessages();    //Clear global queue
    }
}
