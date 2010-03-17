package team276;

import battlecode.common.*;

public abstract class Bot {
    protected static final int MAX_BOTS_SCAN = 10;

    protected final RobotController rc;
    protected final Robot self;
    protected RobotInfo status;

    protected int bcCounterStart;

    protected final RobotInfo alliedAir[];
    protected final RobotInfo alliedGround[];
    protected final RobotInfo enemyAir[];
    protected final RobotInfo enemyGround[];
    protected int nAlliedAir;
    protected int nAlliedGround;
    protected int nEnemyAir;
    protected int nEnemyGround;

    public Bot(RobotController rc) throws Exception {
        this.rc = rc;
        this.self = rc.getRobot();
        this.status = rc.senseRobotInfo(self);
        this.alliedAir = new RobotInfo[MAX_BOTS_SCAN];
        this.alliedGround = new RobotInfo[MAX_BOTS_SCAN];
        this.enemyAir = new RobotInfo[MAX_BOTS_SCAN];
        this.enemyGround = new RobotInfo[MAX_BOTS_SCAN];
        this.nAlliedAir = 0;
        this.nAlliedGround = 0;
        this.nEnemyAir = 0;
        this.nEnemyGround = 0;
    }

    public abstract void AI() throws Exception;

    // Sense the nearby robots
    // BC: 534 for 5, ~106 per bot
    public final void senseNear() throws Exception {
        Robot[] airUnits;
        Robot[] groundUnits;
        RobotInfo tri;
        int i, len;

        nAlliedAir = 0;
        nAlliedGround = 0;
        nEnemyAir = 0;
        nEnemyGround = 0;

        // BC: 104 for 5, ~20 per bot for each call.
        airUnits = rc.senseNearbyAirRobots();
        groundUnits = rc.senseNearbyGroundRobots();

        // BC: 303 for 5, ~60 per bot for this loop
        len = airUnits.length;
        for(i = 0; i < len && i < MAX_BOTS_SCAN; i++) {
            tri = rc.senseRobotInfo(airUnits[i]);

            if(status.team.equals(tri.team))
                alliedAir[nAlliedAir++] = tri;

            // The team of the bot could be neutral so we HAVE to do this check.
            else if(status.team.opponent().equals(tri.team))
                enemyAir[nEnemyAir++] = tri;
        }

        len = groundUnits.length;
        for(i = 0; i < len && i < MAX_BOTS_SCAN; i++) {
            tri = rc.senseRobotInfo(groundUnits[i]);

            if(status.team.equals(tri.team))
                alliedGround[nAlliedGround++] = tri;

            // The team of the bot could be neutral so we HAVE to do this check.
            else if(status.team.opponent().equals(tri.team))
                enemyGround[nEnemyGround++] = tri;
        }
    }
}
