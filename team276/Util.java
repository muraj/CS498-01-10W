package team276;

import battlecode.common.*;

public class Util {
    private final static MapLocation ZERO = new MapLocation(0,0);
    public static final Direction coordToDirection(int dx, int dy) {
        return ZERO.directionTo(new MapLocation(dx, dy));
    }
}
