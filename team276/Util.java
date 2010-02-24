package team276;

import battlecode.common.*;

public class Util {
    private final static MapLocation zero = new MapLocation(0,0);
    public static Direction coordToDirection(int dx, int dy) {
        return zero.directionTo(new MapLocation(dx, dy));
    }
}
