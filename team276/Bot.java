package team276;

import battlecode.common.*;

public abstract class Bot {
    protected static final int MAX_BOTS_SCAN = 10;

    protected final RobotController rc;
    protected final Robot self;
    protected RobotInfo status;

    protected int bcCounterStart;

    protected final RobotInfo alliedUnits[];
    protected final RobotInfo enemyUnits[];
    protected int nAlliedUnits;
    protected int nEnemyUnits;

    public Bot(RobotController rc) throws Exception {
        this.rc = rc;
        this.self = rc.getRobot();
        this.status = rc.senseRobotInfo(self);
        this.alliedUnits = new RobotInfo[MAX_BOTS_SCAN];
        this.enemyUnits = new RobotInfo[MAX_BOTS_SCAN];
        this.nAlliedUnits = 0;
        this.nEnemyUnits = 0;
    }

    public abstract void AI() throws Exception;

    // Sense the nearby robots
    // BC: 528 for 5, ~105 per bot
    public final void senseNear() throws Exception {
        Robot[] airUnits;
        Robot[] groundUnits;
        RobotInfo tri;
        int i, len;

        nAlliedUnits = 0;
        nEnemyUnits = 0;

        // BC: 104 for 5, ~20 per bot for each call.
        airUnits = rc.senseNearbyAirRobots();
        groundUnits = rc.senseNearbyGroundRobots();

        // BC: 303 for 5, ~60 per bot for this loop
        len = airUnits.length;
        for(i = 0; i < len && i < MAX_BOTS_SCAN; i++) {
            tri = rc.senseRobotInfo(airUnits[i]);

            if(status.team.equals(tri.team))
                alliedUnits[nAlliedUnits++] = tri;

            // The team of the bot could be neutral so we HAVE to do this check.
            else if(status.team.opponent().equals(tri.team))
                enemyUnits[nEnemyUnits++] = tri;
        }

        len = groundUnits.length;
        for(i = 0; i < len && i < MAX_BOTS_SCAN; i++) {
            tri = rc.senseRobotInfo(groundUnits[i]);

            if(status.team.equals(tri.team))
                alliedUnits[nAlliedUnits++] = tri;

            // The team of the bot could be neutral so we HAVE to do this check.
            else if(status.team.opponent().equals(tri.team))
                enemyUnits[nEnemyUnits++] = tri;
        }
    }
}
