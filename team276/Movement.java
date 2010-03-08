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
        Debugger.debug_print("Setting direction to: " + dir.name());

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
        Debugger.debug_print("move(): CL " + bot.currentLocation + " target: " + target);
        if(bot.currentLocation.equals(target)) {
            Debugger.debug_print("We're here! Get the hell out of the bus!");
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
            Debugger.debug_print("We're not facing the right way! Fix it.");
            setDirection(dir);
        }

        else {
            Debugger.debug_print("We appear to be facing the correct direction. Do the move.");
            moveForward();
        }
    }

    private void doFollowPath() throws Exception {
        Debugger.debug_print("Following Path...");
        Direction td;
        TerrainTile.TerrainType tt;

        // Check and see if we can actually move to the next direct tile 
        td = bot.currentLocation.directionTo(target);
        attMove = bot.currentLocation.add(td);
        tt = bot.rc.senseTerrainTile(attMove).getType();

        if(td == Direction.OMNI)
            Debugger.debug_print("We're here.");

        // FIXME FIXME FIXME
        // This next check does land too, which really isn't nessecary. This is only to debug our
        // current pathing approach on archons to make sure it'll work for land units.
        if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(td)) {
            Debugger.debug_print("This tile is open and movable");
            makeMoveOrFace(td);
        } 
        
        else {
            if(tt != TerrainTile.TerrainType.LAND) {
                Debugger.debug_print("The tile IS NOT land");
                doFollowObject();
            }

            else {
                Debugger.debug_print("There's probably another bot in our way.");
                doFollowUnit();
            }
        }
    }

    private void doFollowObject() throws Exception {
        Debugger.debug_print("There's an object in the way. Attempting to find a way around it.");
        TerrainTile.TerrainType tt;
        Direction td;

        // If this is the first time through, set the direction of the object we're following
        // and pick a way to go around it.
        if(moveType != Status.OBJECT) {
            dToObstacle = bot.currentLocation.directionTo(attMove);
            td = dToObstacle;
            moveType = Status.OBJECT;
            Debugger.debug_print("dToObstacle: " + dToObstacle.name());

            // Find a direction that avoids what's in front of us
            do {
                td = td.rotateLeft();
                tt = bot.rc.senseTerrainTile(bot.currentLocation.add(td)).getType();

                Debugger.debug_print("Does " + td.name() + " work?");

                if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(td)) {
                    Debugger.debug_print("YES!"); avoidanceDir = td;
                    Debugger.debug_print("avoidanceDir: " + avoidanceDir.name());

                    // Fudge dToObstacle to optimize when we get to an edge of an object
                    dToObstacle = uglyPOS();
                    makeMoveOrFace(avoidanceDir);
                    break;
                }

                Debugger.debug_print("NO!");
            } while(true);
        }

        // We already have an avoidance direction.
        else {
            // If we can finally move to our obstacle direction, do it.
            tt = bot.rc.senseTerrainTile(bot.currentLocation.add(dToObstacle)).getType();
            if(tt == TerrainTile.TerrainType.LAND && bot.rc.canMove(dToObstacle)) {
                Debugger.debug_print("We can move towards our target! :)");
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
                moveType = Status.PATH;
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
