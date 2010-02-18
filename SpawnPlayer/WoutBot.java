package SpawnPlayer;

import battlecode.common.*;

public class WoutBot extends Bot {
    public final MapLocation ZERO = new MapLocation(0,0);
    public WoutBot(RobotController rc, Team t) {
        super(rc,t);
    }

    public void AI() throws Exception {
        while (true) {
            Debugger.debug_print("I'm a Wout!");
            Debugger.debug_print_energon(this.rc);
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
        int[] cohesion=new int[2], seperation=new int[2], align=new int[2];
        MapLocation myloc = rc.getLocation();
        /* General swarm */
        for (Robot r: rc.senseNearbyGroundRobots()) {
            RobotInfo ri = rc.senseRobotInfo(r);
            if (rc.getTeam() == ri.team) continue;
            cohesion[0]+=ri.location.getX();
            cohesion[1]+=ri.location.getY();
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
        double clen = ZERO.distanceSquaredTo(new MapLocation(cohesion[0], cohesion[1]));
        double slen = ZERO.distanceSquaredTo(new MapLocation(seperation[0], seperation[1]));
        double alen = ZERO.distanceSquaredTo(new MapLocation(align[0], align[1]));
        clen = clen == 0 ? 1 : clen;
        slen = slen == 0 ? 1 : slen;
        alen = alen == 0 ? 1 : alen;
        glen = glen == 0 ? 1 : glen;
        //Debugger.debug_print("cohesion: "+cohesion[0]+" "+cohesion[1]);
        //Debugger.debug_print("seperation: "+seperation[0]+" "+seperation[1]);
        //Debugger.debug_print("alignment: "+align[0]+" "+align[1]);
        //Debugger.debug_print("goal: "+ (leader.getX() - myloc.getX()) + " " +(leader.getY() - myloc.getY()));
        double outx = cohesion[0]*COHESION/Math.sqrt(clen)
                      + seperation[0]*SEPERATION/Math.sqrt(slen)
                      + align[0]*ALIGNMENT/Math.sqrt(alen)
                      + (leader.getX() - myloc.getX())*GOAL/glen;
        double outy = cohesion[1]*COHESION/Math.sqrt(clen)
                      + seperation[1]*SEPERATION/Math.sqrt(slen)
                      + align[1]*ALIGNMENT/Math.sqrt(alen)
                      + (leader.getY() - myloc.getY())*GOAL/glen;
        //Debugger.debug_print(outx+" "+outy);
        //Debugger.debug_print("direction: "+ ZERO.directionTo(new MapLocation((int)(outx*100), (int)(outy*100))));
        return ZERO.directionTo(new MapLocation((int)(outx*100), (int)(outy*100)));
    }
}
