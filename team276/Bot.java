package team276;

import battlecode.common.*;

public abstract class Bot {
    protected final RobotController rc;
    protected final Team team;
    protected final int id;
    protected int bcCounterStart;
    protected static final int X = 0;
    protected static final int Y = 1;
    protected MapLocation currentLocation;

    public Bot(RobotController rc, Team t) {
        this.rc = rc;
        this.id = rc.getRobot().getID();
        this.team = t;
        this.currentLocation = rc.getLocation();
    }

    public abstract void AI() throws Exception;

    public void yield(){
        rc.yield();
    }

   /*
    * sensorScan() -- Scans the squares relative to the bot specified by
    * scanOffs. This doesn't take into account the bot's heading.
    *
    * I.E:
    *  scanOffs = { { 0, -1 } } will always scan the square north of the bot's
    *  current position.
    *
    * 0,0 ----------> x,0
    *  |
    *  |
    *  |
    *  |
    *  o
    * y,0             x,y
    *
    * MAP_MIN_WIDTH  <= x <= MAP_MAX_WIDTH
    * MAP_MIN_HEIGHT <= y <= MAP_MAX_HEIGHT
    */
    public void sensorScan(int[][] scanOffs) {
        int nScanOffs = scanOffs.length;
        int myX = currentLocation.getX();
        int myY = currentLocation.getY();

        for(int i = 0; i < nScanOffs; i++) {
            TerrainTile tTT;
            tTT = rc.senseTerrainTile(new MapLocation(myX + scanOffs[i][X], myY + scanOffs[i][Y]));
        }

        Debugger.debug_print("sensorScan(): Finished");
    }
}
