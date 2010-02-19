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
    public final double SEPERATION = 1.0;
    public final double COHESION = 1.0;
    public final double ALIGNMENT = 1.0;
    public final double GOAL = 1.0;

    public Direction flock() throws Exception {
        int[] seperation=new int[2], align=new int[2];
        MapLocation myloc = rc.getLocation();
        /* General swarm */
        Robot[] rl = rc.senseNearbyGroundRobots();
        for (Robot r: rl) {
            RobotInfo ri = rc.senseRobotInfo(r);
            if (rc.getTeam() != ri.team) continue;
            seperation[0]+= myloc.getX() - ri.location.getX();
            seperation[1]+= myloc.getY() - ri.location.getY();
            align[0]+=ri.directionFacing.dx;
            align[1]+=ri.directionFacing.dy;
        }
        /* LEADER GOAL */
        MapLocation leader = null;
        double glen = Double.MAX_VALUE;
        for (MapLocation t : rc.senseAlliedArchons()) {
            double tdist = myloc.distanceSquaredTo(t);
            if (tdist < glen) {
                leader = t;
                glen = tdist;
            }
        }
        /* Calculate Vector lengths */
        double slen = ZERO.distanceSquaredTo(new MapLocation(seperation[0], seperation[1]));
        double alen = ZERO.distanceSquaredTo(new MapLocation(align[0], align[1]));
        slen = slen == 0 ? 1 : slen;    //Prevent divide by zero
        alen = alen == 0 ? 1 : alen;
        glen = glen == 0 ? 1 : glen;
        /* Sum the vectors */
        double outx = -seperation[0]/Math.sqrt(slen)*(SEPERATION - COHESION)    //Cohesion == -Seperation
                      + align[0]*ALIGNMENT/Math.sqrt(alen)
                      + (leader.getX() - myloc.getX())*GOAL/glen;
        double outy = -seperation[1]/Math.sqrt(slen)*(SEPERATION - COHESION)
                      + align[1]*ALIGNMENT/Math.sqrt(alen)
                      + (leader.getY() - myloc.getY())*GOAL/glen;
        return ZERO.directionTo(new MapLocation((int)(outx*10), (int)(outy*10)));
    }
}
