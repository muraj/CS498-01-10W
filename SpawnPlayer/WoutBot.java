package SpawnPlayer;

import battlecode.common.*;

public class WoutBot extends Bot {
    public final MapLocation ZERO = new MapLocation(0,0);
    public WoutBot(RobotController rc, Team t) {
        super(rc,t);
    }

    public void AI() throws Exception {
        while (true) {
            while (rc.isMovementActive()) rc.yield();
            Direction f = flock();
            Debugger.debug_print_bc_used();
            if (f != Direction.OMNI) {
                if (rc.canMove(f)) rc.setDirection(f);
                else if (rc.canMove(f.rotateLeft())) rc.setDirection(f.rotateLeft());
                else rc.setDirection(f.rotateRight());
                rc.yield();
            }
            if (rc.canMove(rc.getDirection()))
                rc.moveForward();
            rc.yield();
        }
    }
    private static final double SEPERATION = 1.0;
    private static final double COHESION = 1.0;
    private static final double ALIGNMENT = 1.0;
    private static final double COLLISION = 1.0;
    private static final double GOAL = 1.0;
    private static final int MAX_GROUP_SZ = 3;

    public Direction flock() throws Exception {
        int[] seperation=new int[2], align=new int[2], collision=new int[2];
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
        Debugger.debug_print_bc_used();
        Debugger.debug_print_counter(this);
        /* Calculate Vector lengths */
        double slen = ZERO.distanceSquaredTo(new MapLocation(seperation[0], seperation[1]));
        double alen = ZERO.distanceSquaredTo(new MapLocation(align[0], align[1]));
        double clen = ZERO.distanceSquaredTo(new MapLocation(collision[0], collision[1]));
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
                      + (leader.getX() - myloc.getX())*GOAL/glen;
        double outy = -seperation[1]/slen*(SEPERATION - COHESION)
                      + align[1]*ALIGNMENT/alen
                      + collision[1]*COLLISION/clen
                      + (leader.getY() - myloc.getY())*GOAL/glen;
        Debugger.debug_print_counter(this);
        return ZERO.directionTo(new MapLocation((int)(outx*10), (int)(outy*10)));
    }
}
