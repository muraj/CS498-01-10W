package team276;

import battlecode.common.*;
import java.util.HashMap;
//import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.Comparator;

public class WoutBot extends Bot {
    private HashMap<MapLocation, Double> H = new HashMap<MapLocation, Double>(50);
//    private PriorityQueue<MapLocation> q = new PriorityQueue<MapLocation>(50, new DistanceComparator());
//    private double[][] dist;
    private LinkedList<MapLocation> q = new LinkedList<MapLocation>();
    private Direction[] DIRS = { Direction.NORTH, Direction.SOUTH, Direction.EAST,
                                Direction.WEST, Direction.SOUTH_EAST, Direction.SOUTH_WEST,
                                Direction.NORTH_EAST, Direction.NORTH_WEST };
    public WoutBot(RobotController rc) throws Exception {
        super(rc);
    }
    public void AI() throws Exception {
        while (true) {
            MapLocation x = rc.getLocation();
            aStar(new MapLocation(x.getX()+2, x.getY()+2));
            q.clear();
            H.clear();
            rc.yield();
        }
    }
    public final double distWeight(final Direction d){
        return (d.isDiagonal() ? RobotType.WOUT.moveDelayDiagonal()
                : RobotType.WOUT.moveDelayOrthogonal());
    }
    //Straight forward estimation of the distance s->e
    public double estdist(final MapLocation s, final MapLocation e){
        int relx = e.getX() - s.getX();
        int rely = e.getY() - s.getY();
        /**The most direct path to any location is to move diagonally an
        axis of the dest, then orthogonally to it.  The minimum of the rels
        is also the distance diagonally in this case.**/
        if(relx > rely) return rely * (RobotType.WOUT.moveDelayDiagonal()-1) + relx*RobotType.WOUT.moveDelayOrthogonal() + 1;
        return relx * (RobotType.WOUT.moveDelayDiagonal()-1) + rely*RobotType.WOUT.moveDelayOrthogonal() + 1;
    }
    public void aStar(MapLocation e){
         MapLocation s = rc.getLocation();
        if(q.isEmpty()){
            q.add(s);
            //H.put(s, estdist(x,e));     //A*
 //           H.put(s, 0.0);                //Dijkstra
        }
        for(int depth = 0; !q.isEmpty() && depth < 2; depth++){
            Debugger.debug_print("POPPED");
            final MapLocation n = q.poll();
            final double ndist = 0;//H.get(n);      //A* & Dijkstra
            boolean sensorEdge = false;
            for(int i = 0; i< DIRS.length; i++){
                Debugger.debug_setCounter(this);
                MapLocation a = n.add(DIRS[i]);
                if(a.equals(e)){
                    Debugger.debug_print("At the end!");
                    //Clear queue and hash?
                    return;
                }
                TerrainTile t = rc.senseTerrainTile(a);
                if(t!=null && !t.isTraversableAtHeight(RobotLevel.ON_GROUND)) //Sense-able, but can't go there, so skip it.
                    continue;
                //double cost = ndist + estdist(a,e) + distWeight(DIRS[i]);     //A*
                final double cost = ndist + distWeight(DIRS[i]);                //Dijkstra
//                if(!H.containsKey(a) || H.get(a) > cost)        //A* & Dijkstra
//                    H.put(a, cost);  //Isn't optimal for going backwards too.
                q.add(a);
                sensorEdge = (t == null);
                Debugger.debug_print("HERE");
                Debugger.debug_printCounter(this);
            }
            if(sensorEdge) break;   //If one of the nodes was at our sensor edge, then move along the best path til we see more.
        }
    }
/*    public class DistanceComparator implements Comparator<MapLocation> {  //A*
        public int compare(final MapLocation m1, final MapLocation m2) {
            double x = H.get(m1) - H.get(m2);
            return x >= 0 ? (x>0 ? 1 : 0) : -1;
        }
    }
*/
}
