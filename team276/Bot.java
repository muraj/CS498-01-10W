package team276;

import battlecode.common.*;
import java.util.Arrays;

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

    protected RobotInfo highPriorityEnemy;
    protected RobotInfo highPriorityAlliedArchon;
    protected RobotInfo highPriorityAlliedGround;
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
        this.highPriorityAlliedArchon = null;
        this.highPriorityAlliedGround = null;
        this.nAlliedAir = 0;
        this.nAlliedGround = 0;
        this.nEnemyAir = 0;
        this.nEnemyGround = 0;
    }

    // We assign units a priority based on the scanning robot's ability to attack, "LH" threshold,
    // and amount of remaining health. Of two robots of the same type and "LH/HH" class, the one 
    // with lower remaining health should recieve the higher priority.
    public int calcEnemyPriority(RobotInfo ri) {
        // FIXME: Tweak these for best results
        final int LH_ARCHON         = 15;       // When do we consider these units "low health?"
        final int LH_CHAINER        = 10;
        final int LH_SOLDIER        = 10;
        final int LH_TURRET         = 15;
        final int LH_WOUT           = 10;
        final int LH_TOWER          = 10;

        final int LH_ARCHON_PV      = 1100;     // Base priority value for this low health unit
        final int LH_CHAINER_PV     = 1000;
        final int LH_SOLDIER_PV     = 900;
        final int LH_TURRET_PV      = 800;
        final int LH_WOUT_PV        = 700;
        final int LH_TOWER_PV       = 600;

        final int HH_CHAINER_PV     = 500;      // Base priority value for high health units
        final int HH_SOLDIER_PV     = 400;
        final int HH_TURRET_PV      = 300;
        final int HH_WOUT_PV        = 200;
        final int HH_ARCHON_PV      = 100;
        final int HH_TOWER_PV       = 0;

        int hv;
        int tel;

        // If we can't attack it, ignore it
        // FIXME: If we can't attack as far as we can sense, maybe this should get reworked
        // so that movement "pulls" this bot towards this enemy if it's the only target
        // available. At the same time, that might pull him out of flocking and force a 1v1?
        if(!rc.canAttackSquare(ri.location))
            return -1;

        // If the game updates the way I *HOPE* it does (the robots energon is calculated at the
        // end of every other robots turn in case it's attacked) then this will filter out any
        // bots that could be in a state of "limbo" if they're not cleaned off the map until the
        // end of the round.
        // FIXME: Figure out what the game engine behavior is here.. Does it immediately remove FIXME
        // FIXME: dead bots at the end of every robots turn? Or are they stuck here until the   FIXME
        // FIXME: end of the round? Is they're health updated at the end of every turn? Ugh.    FIXME

        tel = (int)ri.energonLevel;

        // If the robot is "dead", ignore it.
        // FIXME: Read above comment.. does this work the way I think/hope it does?
        if(tel <= 0)
            return -1;

        hv = (int)ri.maxEnergon - tel;

        switch(ri.type) {
            case ARCHON: return (tel <= LH_ARCHON) ? LH_ARCHON_PV + hv : HH_ARCHON_PV + hv; 
            case CHAINER: return (tel <= LH_CHAINER) ? LH_CHAINER_PV + hv : HH_CHAINER_PV + hv; 
            case SOLDIER: return (tel <= LH_SOLDIER) ? LH_SOLDIER_PV + hv : HH_SOLDIER_PV + hv; 
            case WOUT: return (tel <= LH_WOUT) ? LH_WOUT_PV + hv : HH_WOUT_PV + hv; 
            case TURRET: return (tel <= LH_TURRET) ? LH_TURRET_PV + hv : HH_TURRET_PV + hv; 

            // Towers just ignore specializations for now
            case AURA:
            case TELEPORTER:
            case COMM: return (tel <= LH_TOWER) ? LH_TOWER_PV + hv : HH_TOWER_PV + hv; 
        }

        return -1;
    }

    // What did we want this to do? Return a highy priority target for energon transfer?
    public int calcAlliedPriority(RobotInfo ri) {
        return -1;
    }
    // Sense the nearby robots
    // With the new checks and enemy prioritizing:
    // Friends: 569 for 5, ~113/friendly
    // Enemies: 695 for 5 friendlies, 1 enemy, (695-569) = ~126/enemy
    // TODO: Figure out where to deal with attacker messgaes from others:
    // "Only care about those messgaes when you don't have a good local target"
    public final void senseNear() throws Exception {
        Robot[] airUnits;
        Robot[] groundUnits;
        RobotInfo tri;
        int highPriorityEnemyValue, highPriorityAlliedValue;
        int i, len, thpa, thpe;

        highPriorityEnemyValue = 0;
        highPriorityAlliedValue = 0;

        nAlliedAir = 0;
        nAlliedGround = 0;
        nEnemyAir = 0;
        nEnemyGround = 0;

        airUnits = rc.senseNearbyAirRobots();
        groundUnits = rc.senseNearbyGroundRobots();

        // Air units
        len = airUnits.length;
        if(len > MAX_BOTS_SCAN)
            len = MAX_BOTS_SCAN;

        for(i = 0; i < len; i++) {
            tri = rc.senseRobotInfo(airUnits[i]);

            if(status.team.equals(tri.team)) {
                alliedAir[nAlliedAir++] = tri;

                thpa = calcAlliedPriority(tri);
                if(thpa > highPriorityAlliedValue) {
                    highPriorityAlliedValue = thpa;
                    highPriorityAlliedArchon = tri;
                }
            }

            else {
                enemyAir[nEnemyAir++] = tri;

                thpe = calcEnemyPriority(tri);
                if(thpe > highPriorityEnemyValue) {
                    highPriorityEnemyValue = thpe;
                    highPriorityEnemy = tri;
                }
            }
        }

        // Repeat for ground units.
        highPriorityAlliedValue = 0;
        len = groundUnits.length;
        if(len > MAX_BOTS_SCAN)
            len = MAX_BOTS_SCAN;

        for(i = 0; i < len; i++) {
            tri = rc.senseRobotInfo(groundUnits[i]);

            if(status.team.equals(tri.team)) {
                alliedGround[nAlliedGround++] = tri;

                thpa = calcAlliedPriority(tri);
                if(thpa > highPriorityAlliedValue) {
                    highPriorityAlliedValue = thpa;
                    highPriorityAlliedGround = tri;
                }
            }
 
            else {
                enemyGround[nEnemyGround++] = tri;

                thpe = calcEnemyPriority(tri);
                if(thpe > highPriorityEnemyValue) {
                    highPriorityEnemyValue = thpe;
                    highPriorityEnemy = tri;
                }
            }
        }
    }
}
