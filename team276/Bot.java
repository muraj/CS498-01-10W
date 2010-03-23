package team276;

import battlecode.common.*;
import java.util.PriorityQueue;
import java.util.Arrays;

public abstract class Bot {
    protected static final int MAX_BOTS_SCAN    = 10;
    protected static final int MAX_ARCHON_SCAN  = 6;
    protected static final int MAX_MAP_DIM_SQ   = GameConstants.MAP_MAX_HEIGHT*GameConstants.MAP_MAX_HEIGHT;
    protected final int MAX_GROUP_SZ            = 3;

    protected final RobotController rc;
    protected final Robot self;
    protected RobotInfo status;

    protected int bcCounterStart;
    protected PriorityQueue<ParsedMsg> msgQueue;

    protected MapLocation alliedArchons[];
    protected MapLocation highPriorityAlliedArchon;

    protected final RobotInfo alliedGround[];
    protected final RobotInfo enemyAir[];
    protected final RobotInfo enemyGround[];

    protected RobotInfo highPriorityEnemy;
    protected RobotInfo highPriorityAlliedGround;
    protected int nAlliedAir;
    protected int nAlliedGround;
    protected int nEnemyAir;
    protected int nEnemyGround;

    protected Direction queuedMoveDirection;
    protected boolean movementDelay = false;

    public abstract void AI() throws Exception;

    public Bot(RobotController rc) throws Exception {
        this.rc = rc;
        this.self = rc.getRobot();
        this.status = rc.senseRobotInfo(self);
        this.msgQueue = new PriorityQueue<ParsedMsg>(10, new Util.MessageComparator());
        this.alliedArchons = null;
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
        this.queuedMoveDirection = null;
    }

    public final void resetMsgQueue() {
        msgQueue = new PriorityQueue<ParsedMsg>(10, new Util.MessageComparator());
    }

    public int calcAlliedArchonPriority(MapLocation ml) {
        return MAX_MAP_DIM_SQ - status.location.distanceSquaredTo(ml);
    }

    public int calcAlliedPriority(RobotInfo ri) {
        return -1;
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

    public RobotController getRC() {
        return rc;
    }

    public Direction flock(double SEPERATION, double COHESION, double ALIGNMENT, double COLLISION, double GOAL) throws Exception {
        int[] seperation=new int[2], align=new int[2], goal=new int[2], collision=new int[2];
        MapLocation myloc = status.location;
        /* General swarm */
        int c = 0;
        int len;

        len = nAlliedGround;
        if(len > MAX_GROUP_SZ)
            len = MAX_GROUP_SZ;

        for(c = 0; c < len; c++) {
            RobotInfo ri = alliedGround[c];

            seperation[0]+= myloc.getX() - ri.location.getX();
            seperation[1]+= myloc.getY() - ri.location.getY();
            align[0]+=ri.directionFacing.dx;
            align[1]+=ri.directionFacing.dy;
        }
        /* COLLISION GOAL */
        if (COLLISION != 0) {
            for (Direction d : Direction.values()) {
                if (d == Direction.OMNI || d == Direction.NONE) continue;
                TerrainTile t = rc.senseTerrainTile(myloc.add(d));
                if (t != null && t.getType() != TerrainTile.TerrainType.LAND) {
                    collision[0] -= d.dx;
                    collision[1] -= d.dy;
                }
            }
        }
        /* LEADER GOAL */
        if (GOAL != 0) {
            MapLocation leader = highPriorityAlliedArchon;
            if (leader != null && rc.canSenseSquare(leader)) {
                Direction leader_dir = rc.senseRobotInfo(rc.senseAirRobotAtLocation(leader)).directionFacing;
                goal[0] = leader.getX()+5*leader_dir.dx - myloc.getX();
                goal[1] = leader.getY()+5*leader_dir.dy - myloc.getY();
            } else if (leader != null) {
                goal[0] = leader.getX() - myloc.getX();
                goal[1] = leader.getY() - myloc.getY();
            }
        }

        /* Calculate Vector lengths */
        double slen = Util.ZERO.distanceSquaredTo(new MapLocation(seperation[0], seperation[1]));
        double alen = Util.ZERO.distanceSquaredTo(new MapLocation(align[0], align[1]));
        double clen = Util.ZERO.distanceSquaredTo(new MapLocation(collision[0], collision[1]));
        double glen = GOAL != 0 ? Math.sqrt(Util.ZERO.distanceSquaredTo(new MapLocation(goal[0], goal[1]))) : 1;

        slen = slen == 0 ? 1 : Math.sqrt(slen);    //Prevent divide by zero
        alen = alen == 0 ? 1 : Math.sqrt(alen);
        clen = clen == 0 ? 1 : Math.sqrt(clen);
        /* Sum the vectors */
        double outx = -seperation[0]/slen*(SEPERATION - COHESION)    //Cohesion == -Seperation
                      + align[0]*ALIGNMENT/alen
                      + collision[0]*COLLISION/clen
                      + goal[0]*GOAL/glen;
        double outy = -seperation[1]/slen*(SEPERATION - COHESION)
                      + align[1]*ALIGNMENT/alen
                      + collision[1]*COLLISION/clen
                      + goal[1]*GOAL/glen;
        return Util.coordToDirection((int)(outx*10), (int)(outy*10));
    }

    public final void processMsgs(int MAXBC) throws Exception {
        Message m;
        int startbc = Clock.getBytecodeNum();
        while ((m = rc.getNextMessage()) != null) {
            if (m.ints == null || m.ints.length < 3) continue;
            if (m.ints[0] != ParsedMsg.chksum(m)) continue;
            switch (MSGTYPE.values()[m.ints[2]]) {
            case BEACON:
                msgQueue.add(new Beacon(m));
                break;
            case ATTACK:
                //msgQueue.add(new Attack(m));
                break;
            }
            if (Clock.getBytecodeNum() - startbc >= MAXBC) break;
        }
        rc.getAllMessages();    //Clear global queue - may loose messages, but they'll be old anyway
    }

    //Uses RobotInfo highPriorityEnemy as our target.
    public boolean attack() throws Exception {
        //If we...
        //  1.) Don't have a target
        //  2.) Are On attack cooldown
        //  3.) Can't attack the square our enemy is on
        //We can't attack this round. Return out so we can try and move.

        if( highPriorityEnemy == null
            || status.roundsUntilAttackIdle != 0
            || !rc.canAttackSquare(highPriorityEnemy.location)) {

            return false;
        }
        //Attacking takes higher priority than movement.
        //If we have an action set from the previous round (movement or direction),
        //reset their global flags and remove it from the queue since we have a target to attack.
        if(rc.hasActionSet()) {
            resetMovementFlags();
            rc.clearAction();
        }

        //Call the proper attack call if our enemy is an airbore ARCHON.
        if(highPriorityEnemy.type == RobotType.ARCHON) {
            rc.attackAir(highPriorityEnemy.location);
        }
        else {
            rc.attackGround(highPriorityEnemy.location);
        }
        return true;
    }

    public void handleMovement() throws Exception {
        //On movement cooldown, can't do anything here anyways.
        if(status.roundsUntilMovementIdle != 0)
            return;

        //Have an attack action in our queue.
        //Attack has higher priority, so we concede movement on this round.
        if(rc.hasActionSet() && queuedMoveDirection == null && !movementDelay)
            return;

        //No action on the queue and no direction set, lets find our next move.
        if(queuedMoveDirection == null) {
            queuedMoveDirection = flock(1,1,1,1,1);

            //If we magically got a direction to our current location, or worse, just quit now.
            if(queuedMoveDirection == Direction.OMNI || queuedMoveDirection == Direction.NONE)
                return;

            //If we're currently facing our target direction...
            if(status.directionFacing == queuedMoveDirection) {
                //If we can move forward, do it.
                if(rc.canMove(queuedMoveDirection)) {
                    resetMovementFlags();
                    rc.moveForward();
                    return;
                }
                //If we cant move forward this round, flag our single round delay.
                else movementDelay = true;
            }
            //If our target destination is behind us, don't change direction...
            else if(status.directionFacing == queuedMoveDirection.opposite()) {
                //If we can move backward, do it.
                if(rc.canMove(queuedMoveDirection.opposite())) {
                    queuedMoveDirection = null;
                    rc.moveBackward();
                    return;
                }
                //If we can't move backward, just flag our single round delay.
                else movementDelay = true;
            }
        }
        //We have a movement direction in our queue...
        else {
            //If we can move forward, do it.
            if(rc.canMove(queuedMoveDirection)) {
                rc.moveForward();
            }
            //If we can't move forward...
            else {
                if(!movementDelay){
                    movementDelay = true;
                    return;
                }
                resetMovementFlags();
            }
        }
    }

    private final void resetMovementFlags() {
        queuedMoveDirection = null;
        movementDelay = false;
    }

    // Sense the nearby robots
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

        highPriorityEnemy = null;
        highPriorityAlliedArchon = null;

        airUnits = rc.senseNearbyAirRobots();
        groundUnits = rc.senseNearbyGroundRobots();

        // Air units
        len = airUnits.length;
        if(len > MAX_BOTS_SCAN)
            len = MAX_BOTS_SCAN;

        // Only deal with enemy air
        for(i = 0; i < len; i++) {
            tri = rc.senseRobotInfo(airUnits[i]);

            if(status.team.equals(tri.team.opponent())) {
                enemyAir[nEnemyAir++] = tri;

                thpe = calcEnemyPriority(tri);
                if(thpe > highPriorityEnemyValue) {
                    highPriorityEnemyValue = thpe;
                    highPriorityEnemy = tri;
                }
            }
        }

        alliedArchons = rc.senseAlliedArchons();
        len = alliedArchons.length;

        // Our archons
        for(nAlliedAir = 0; nAlliedAir < len; nAlliedAir++) {
            thpa = calcAlliedArchonPriority(alliedArchons[nAlliedAir]);

            if(thpa > highPriorityAlliedValue) {
                highPriorityAlliedValue = thpa;
                highPriorityAlliedArchon = alliedArchons[nAlliedAir];
            }
        }

        nAlliedAir++;

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

    public void yield() {
        rc.yield();
    }
}
