package team276;

import battlecode.common.*;

public class Movement {
    private Bot bot;                        // The bot this movement is for
    private enum Status {                   // What type of movement are we doing?
        PATH,                               // Moving along a direct path
        OBJECT,                             // Attempting to move around an object (voids)
        UNIT                                // Attempting to move around a unit
    };

    private Status moveType;
    private final int MAXPATHLEN = 60;      // This shouldn't be larger than 60 for a 60x60 map
    protected MapLocation target;           // Movement target location
    protected MapLocation attMove;          // The map location of the attempted move.
    protected Direction dToObstacle;        // If we're following an obstacle, the direction to it
    protected Direction avoidanceDir;       // A direction to avoid an obstacle
    protected Boolean bypassObstacle;       // FIXME: HACK: If this is set, make an immediate move in the current direction.

    public Movement(Bot bot, MapLocation target) {
        initCommon(bot, target);
    }

    public Movement(Bot bot, int tX, int tY) {
        initCommon(bot, new MapLocation(tX, tY));
    }

    private void moveForward() throws Exception {
        Debugger.debug_print("Moving forward to: " + bot.currentLocation.add(bot.currentDirection));

        // Safety first! Do some checks to make sure we can actually queue up this move without
        // blowing ourself up.
        if(bot.rc.canMove(bot.currentDirection) && (bot.rc.getRoundsUntilMovementIdle() == 0))
            bot.rc.moveForward();

        else {
            Debugger.debug_print("YOUR CODE FUCKED UP!");
            bot.bp();
        }
    }

    private void setDirection(Direction dir) throws Exception {
        // Make sure we don't somehow already have a move queued.
        if(bot.rc.getRoundsUntilMovementIdle() == 0)
            bot.rc.setDirection(dir);
        else {
            Debugger.debug_print("YOUR CODE FUCKED UP!");
            bot.bp();
        }
    }

    private void initCommon(Bot bot, MapLocation target) {
        Debugger.debug_print("New movement to: " + target);
        this.bot            = bot;
        this.target         = target;
        this.moveType       = Status.PATH;
        this.bypassObstacle = false;

        Debugger.debug_print("initCommon(): CL " + bot.currentLocation + " target: " + target);
    }

    // move() -- If this returns false, we have no more moves to make. True otherwise
    public boolean move() throws Exception {
        if(bot.currentLocation.equals(target)) {
            target          = null;
            moveType        = null;
            bot.bp();
            return false;
        }

        // FIXME: HACK
        if(bypassObstacle) {
            moveForward();
            bypassObstacle = false;
            return true;
        }

        switch(moveType) {
            case PATH: doFollowPath(); break;
            case OBJECT: doFollowObject(); break;
            case UNIT: doFollowUnit(); break;
            default: Debugger.debug_print("move(): Unkown moveType!");
        }

        return true;
    }

    private void makeMoveOrFace(Direction dir) throws Exception {
        if(bot.currentDirection != dir) {
            setDirection(dir);
        }

        else {
            moveForward();
        }
    }

    private void doFollowPath() throws Exception {
        Direction td;
        TerrainTile.TerrainType tt;

<<<<<<< HEAD
        // Check and see if we can actually move to the next direct tile 
        td = bot.currentLocation.directionTo(target);
        attMove = bot.currentLocation.add(td);
        tt = bot.rc.senseTerrainTile(attMove).getType();
=======
        td = bot.currentLocation.directionTo(target);
        attMove = bot.currentLocation.add(td);
        tt = bot.rc.senseTerrainTile(attMove).getType();

        if(td == Direction.OMNI)
            Debugger.debug_print("We're here.");
>>>>>>> edbb827eeb2e21e2fba39beca958487cf4a55d89

        // Check and see if we can actually move to the next direct tile 
        // FIXME FIXME FIXME
        // This next check does land too, which really isn't nessecary. This is only to debug our
        // current pathing approach on archons to make sure it'll work for land units.
        if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(td)) {
            makeMoveOrFace(td);
        } 
        
        else {
            if(tt != TerrainTile.TerrainType.LAND) {
                doFollowObject();
            }

            else {
                doFollowUnit();
            }
        }
    }

    private Direction calcAvoidanceMove(Direction dToObstacle) {
        Direction td = dToObstacle;
        Direction tld;
        Direction trd;
        Direction attempt;
        Direction[] dirs = new Direction[7];            // Max of 7, 8 surrounding - 1 in front
        TerrainTile.TerrainType tt;
        MapLocation tml;
        MapLocation[] mls = new MapLocation[7];
        int pms = 0;

/*
        // Get all of the possible moves
        for(int i = 0; i < 7; i++) {
            td = td.rotateLeft();
            tml = bot.currentLocation.add(td);
            tt = bot.rc.senseTerrainTile(tml).getType();

            Debugger.debug_print("Does " + td.name() + " work?");

            if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(td)) {
                Debugger.debug_print("YES!");
                mls[pms] = tml;
                dirs[pms++] = td;
            } else {
                Debugger.debug_print("NO!");
            }
        }

        Debugger.debug_print("possibleh moves");
        for(int i = 0; i < pms; i++)
            Debugger.debug_print("WOO " + dirs[i].name() + " " + mls[i]);

        // If we're coming at the object at an angle, try to continue in sameish
        // direction.
        // FIXME: This is kinda ugly...
        switch(dToObstacle) {
            case NORTH_EAST: return Direction.EAST;
        }

        return dirs[0];
*/
    
        // If 45* to the left works, take it.
        tld = td.rotateLeft();
        tml = bot.currentLocation.add(tld);
        tt = bot.rc.senseTerrainTile(tml).getType();
        Debugger.debug_print("Does " + tld.name() + " work?");

        if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(tld))
            return tld;
        else
            Debugger.debug_print("NO!");

        // If 45* to the right works, take it.
        trd = td.rotateRight();
        tml = bot.currentLocation.add(trd);
        tt = bot.rc.senseTerrainTile(tml).getType();
        Debugger.debug_print("Does " + trd.name() + " work?");

        if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(trd))
            return trd;
        else
            Debugger.debug_print("NO!");

        // If 90* to the left works, take it.
        tld = tld.rotateLeft();
        tml = bot.currentLocation.add(tld);
        tt = bot.rc.senseTerrainTile(tml).getType();
        Debugger.debug_print("Does " + tld.name() + " work?");

        if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(tld))
            return tld;
        else
            Debugger.debug_print("NO!");

        // If 90* to the right works, take it.
        trd = trd.rotateRight();
        tml = bot.currentLocation.add(trd);
        tt = bot.rc.senseTerrainTile(tml).getType();
        Debugger.debug_print("Does " + trd.name() + " work?");

        if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(trd))
            return trd;
        else
            Debugger.debug_print("NO!");

        // If 135* to the left works, take it.
        tld = tld.rotateLeft();
        tml = bot.currentLocation.add(tld);
        tt = bot.rc.senseTerrainTile(tml).getType();
        Debugger.debug_print("Does " + tld.name() + " work?");

        if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(tld))
            return tld;
        else
            Debugger.debug_print("NO!");

        // If 135* to the right works, take it.
        trd = trd.rotateRight();
        tml = bot.currentLocation.add(trd);
        tt = bot.rc.senseTerrainTile(tml).getType();
        Debugger.debug_print("Does " + trd.name() + " work?");

        if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(trd))
            return trd;
        else
            Debugger.debug_print("NO!");

        return null;

     }

    private void doFollowObject() throws Exception {
        TerrainTile.TerrainType tt;
        Direction td;

        // If this is the first time through, set the direction of the object we're following
        // and pick a way to go around it.
        if(moveType != Status.OBJECT) {
<<<<<<< HEAD
            dToObstacle = bot.currentLocation.directionTo(attMove);
            td = dToObstacle;
            moveType = Status.OBJECT;

            // Find a direction that avoids what's in front of us
            do {
                td = td.rotateLeft();
                tt = bot.rc.senseTerrainTile(bot.currentLocation.add(td)).getType();

                if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(td)) {
                    avoidanceDir = td;

                    // Fudge dToObstacle to optimize when we get to an edge of an object
                    dToObstacle = uglyPOS();
                    makeMoveOrFace(avoidanceDir);
                    break;
                }
            } while(true);
=======
            moveType = Status.OBJECT;

            dToObstacle = bot.currentLocation.directionTo(attMove);
            td = dToObstacle;
            Debugger.debug_print("dToObstacle: " + dToObstacle.name());

            avoidanceDir = calcAvoidanceMove(dToObstacle);
            Debugger.debug_print("avoidanceDir: " + avoidanceDir.name());

            // Fudge dToObstacle to optimize when we get to an edge of an object
            //dToObstacle = uglyPOS();
            makeMoveOrFace(avoidanceDir);
>>>>>>> edbb827eeb2e21e2fba39beca958487cf4a55d89
        }

        // We already have an avoidance direction.
        else {
<<<<<<< HEAD
            // If we can finally move to our obstacle direction, do it.
            tt = bot.rc.senseTerrainTile(bot.currentLocation.add(dToObstacle)).getType();
            if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(dToObstacle)) {
=======
            Debugger.debug_print("dToObstacle: " + dToObstacle.name());

            // If we can finally move to our obstacle direction, do it.
            tt = bot.rc.senseTerrainTile(bot.currentLocation.add(dToObstacle)).getType();
            if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(dToObstacle)) {
                Debugger.debug_print("We can move towards our obstacle direction :)");
>>>>>>> edbb827eeb2e21e2fba39beca958487cf4a55d89
                bypassObstacle = true;          // Hack to force us to make the move next turn so we don't get stuck in a loop.
                moveType = Status.PATH;

                makeMoveOrFace(dToObstacle);

                return;
            }

            // Ok, we can't move directly towards our objective yet, make another avoidance move
            tt = bot.rc.senseTerrainTile(bot.currentLocation.add(bot.currentDirection)).getType();
            if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(dToObstacle)) {
                makeMoveOrFace(bot.currentDirection);
            }

            // We've hit another object. Avoid this new one, also.
            else {
                Debugger.debug_print("We've hit another obstacle, captain!");
                moveType = Status.PATH;
                dToObstacle = bot.currentDirection;
                doFollowObject();
            }
        }
    }

    // Say we hit our obstacle while heading NE and our avoidance
    // takes us directly W. The current system would be checking to
    // see if we can move NE, which means we have to move PAST the
    // end of the obstacle by one square so that we can make that
    // backwards move to the NE. Checking to see if the NW square
    // is movable is more desirable in this case.
    private Direction uglyPOS() {
        switch(dToObstacle) {
            case NORTH:
            case NORTH_EAST:
            case NORTH_WEST:
                switch(avoidanceDir) {
                    case EAST: return Direction.NORTH_EAST;
                    case WEST: return Direction.NORTH_WEST;
                }
            break;

            case SOUTH:
            case SOUTH_EAST:
            case SOUTH_WEST:
                switch(avoidanceDir) {
                    case EAST: return Direction.SOUTH_EAST;
                    case WEST: return Direction.SOUTH_WEST;
                }
            break;

            case EAST:
                switch(avoidanceDir) {
                    case NORTH: return Direction.NORTH_EAST;
                    case SOUTH: return Direction.SOUTH_EAST;
                }
            break;

            case WEST:
                switch(avoidanceDir) {
                    case NORTH: return Direction.NORTH_WEST;
                    case SOUTH: return Direction.SOUTH_WEST;
                }
            break;
        }

        return dToObstacle;
    }

    private void doFollowUnit() {
        Debugger.debug_print("There's another idiot in my way. KILL IT WITH FIRE!");
    }
}
