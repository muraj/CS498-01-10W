package team276;

import battlecode.common.*;

public class ArchonBot extends Bot {
    public ArchonBot(RobotController rc, Team t) {
        super(rc,t);
    }

    private void breakout() throws Exception {
        Debugger.debug_print("breakout(): Enter");
        Debugger.debug_print_bc_used();

        /* Sense the friendlies and figure out who's leading */
        MapLocation[] alliedArchons = rc.senseAlliedArchons();
        boolean pLeader = true;
        
        for(MapLocation i: alliedArchons) {
            if(id > rc.senseAirRobotAtLocation(i).getID()) {
                pLeader = false;
                break;
            }
        }

        /* We're the leader. Queue up a message with our ID in it and then attempt
         * to see if we're close to an edge. */
        if(pLeader) {
            Debugger.debug_print("I'm the leader.");
            isLeader = true;
            Message msg = new Message();
            msg.ints = new int[] { id };
            rc.broadcast(msg);

            /* Only scan the far out parts-- if we're near an edge we'll know. */
            int myX = currentLoc.getX();
            int myY = currentLoc.getY();

            /* Check N */
            if(rc.senseTerrainTile(new MapLocation(myX + 0, myY - 6)).getType() == TerrainTile.TerrainType.OFF_MAP)
                Debugger.debug_print("Close to North boundry!");

            /* Check S */
            else if(rc.senseTerrainTile(new MapLocation(myX + 0, myY + 6)).getType() == TerrainTile.TerrainType.OFF_MAP)
                Debugger.debug_print("Close to South boundry!");

            /* Check E */
            if(rc.senseTerrainTile(new MapLocation(myX + 6, myY + 0)).getType() == TerrainTile.TerrainType.OFF_MAP)
                Debugger.debug_print("Close to East boundry!");

            /* Check W */
            else if(rc.senseTerrainTile(new MapLocation(myX + -6, myY + 0)).getType() == TerrainTile.TerrainType.OFF_MAP)
                Debugger.debug_print("Close to West boundry!");
        }

        /* Otherwise, get the incoming message */
        else {
            Message msg = rc.getNextMessage();

            /* XXX Do some sort of message validation here XXX */

            following = msg.ints[0];
            Debugger.debug_print("I'm following bot id: " + following);
        }

        Debugger.debug_print("breakout(): Exit");
        Debugger.debug_print_bc_used();
    }

    public void AI() throws Exception{
        breakout();

        while(true){
            rc.yield();
        }
    }
}
