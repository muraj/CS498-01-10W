package team276;

import battlecode.common.*;

public class Movement {
    private Bot bot;                        // The bot this movement is for
    private final int MAXPATHLEN = 60;      // This shouldn't be larger than 60 for a 60x60 map
    private int moves;                      // Number of moves in our queue
    protected MapLocation from;             // Movement start location
    protected MapLocation target;           // Movement target location
    protected MapLocation[] directPath;     // The direct path from start to target

    public Movement(Bot bot, MapLocation from, MapLocation target) {
        initCommon(bot, from, target);
    }

    public Movement(Bot bot, MapLocation from, int tX, int tY) {
        initCommon(bot, from, new MapLocation(tX, tY));
    }

    public Movement(Bot bot, int fX, int fY, MapLocation target) {
        initCommon(bot, new MapLocation(fX, fY), target);
    }

    public Movement(Bot bot, int fX, int fY, int tX, int tY) {
        initCommon(bot, new MapLocation(fX, fY), new MapLocation(tX, tY));
    }

    private void initCommon(Bot bot, MapLocation from, MapLocation target) {
        Debugger.debug_print("New movement to: " + target);
        this.bot    = bot;
        this.from   = from;
        this.target = target;
        this.moves  = 0;

        crunchDirectPath();
    }

    private void crunchDirectPath() {
        Debugger.debug_print("Creating path from " + from + " to: " + target);
        directPath = new MapLocation[MAXPATHLEN];
        MapLocation curr = from;

        while(!curr.equals(target)) {
            directPath[moves++] = curr;
            curr = curr.add(curr.directionTo(target));
        }

        // Don't forget the last move
        directPath[moves++] = curr;
    }
}
