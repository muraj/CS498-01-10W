package team276;

import battlecode.common.*;
import java.util.PriorityQueue;

public abstract class Bot {
    protected static final int MAX_BOTS_SCAN = 10;
    protected final int MAX_GROUP_SZ = 3;

    protected final RobotController rc;
    protected final Robot self;
    protected RobotInfo status;

    protected int bcCounterStart;
    protected PriorityQueue<ParsedMsg> msgQueue;

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
        this.msgQueue = new PriorityQueue<ParsedMsg>(10, new Util.MessageComparator());
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

    public final void resetMsgQueue() {
        msgQueue = new PriorityQueue<ParsedMsg>(10, new Util.MessageComparator());
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
        Robot[] rl = rc.senseNearbyGroundRobots();
        int c = 0;
        for (Robot r : rl) {
            if (c > MAX_GROUP_SZ) break;    //Try to limit bytecodes.
            RobotInfo ri = rc.senseRobotInfo(r);
            if (rc.getTeam() != ri.team) continue;
            else c++;
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
            MapLocation leader = null;
            double glen = Double.MAX_VALUE;
            for (MapLocation t : rc.senseAlliedArchons()) {
                double tdist = myloc.distanceSquaredTo(t);
                if (tdist < glen) {
                    leader = t;
                    glen = tdist;
                }
            }
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



    public int attack() {
        //return attack(highPriorityTarget); uncomment when dpn pushes sensing class vars to bot.java
        return 0;
    }
    //RETURN VALUE = -1 : If target location is out of attack range or attack queue !isEmpty.
    //RETURN VALUE = >1 : Rounds until next attack is available.
    public int attack(RobotInfo target) throws Exception {
        int atkCooldown = rc.getRoundsUntilAttackIdle();
        if (atkCooldown != 0)
            return atkCooldown;

        if (!rc.canAttackSquare(target.location) || rc.isAttackActive())
            return -1;

        if (target.type == RobotType.ARCHON) {
            if (rc.canAttackAir())
                rc.attackAir(target.location);
        } else {
            if (rc.canAttackGround())
                rc.attackGround(target.location);
        }

        return status.type.attackDelay();
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

    public void yield() {
        rc.yield();
    }
}
