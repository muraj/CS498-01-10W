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
    protected final RobotInfo highPriorityEnemy;
    protected int nAlliedAir;
    protected int nAlliedGround;
    protected int nEnemyAir;
    protected int nEnemyGround;

    public abstract void AI() throws Exception;

    public Bot(RobotController rc) throws Exception {
        this.rc = rc;
        this.self = rc.getRobot();
        this.status = rc.senseRobotInfo(self);
        this.alliedAir = new RobotInfo[MAX_BOTS_SCAN];
        this.alliedGround = new RobotInfo[MAX_BOTS_SCAN];
        this.enemyAir = new RobotInfo[MAX_BOTS_SCAN];
        this.enemyGround = new RobotInfo[MAX_BOTS_SCAN];
        this.highPriorityEnemy = null;
        this.nAlliedAir = 0;
        this.nAlliedGround = 0;
        this.nEnemyAir = 0;
        this.nEnemyGround = 0;
    }

    // Sense the nearby robots
    // BC: 525 for 5, ~105 per bot
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

        // BC: 291 for 5, ~58 per bot for this loop
        len = airUnits.length;
        if(len > MAX_BOTS_SCAN)
            len = MAX_BOTS_SCAN;

        for(i = 0; i < len; i++) {
            tri = rc.senseRobotInfo(airUnits[i]);

            if(status.team.equals(tri.team))
                alliedAir[nAlliedAir++] = tri;

            else
                enemyAir[nEnemyAir++] = tri;
        }

        // Repeat for ground units.
        len = groundUnits.length;
        if(len > MAX_BOTS_SCAN)
            len = MAX_BOTS_SCAN;

        for(i = 0; i < len; i++) {
            tri = rc.senseRobotInfo(groundUnits[i]);

            if(status.team.equals(tri.team))
                alliedGround[nAlliedGround++] = tri;

            else
                enemyGround[nEnemyGround++] = tri;
        }
    }
}
