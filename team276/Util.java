package team276;

import battlecode.common.*;

public class Util {
    public final static MapLocation ZERO = new MapLocation(0,0);
    public static Direction coordToDirection(int dx, int dy) {
        return ZERO.directionTo(new MapLocation(dx, dy));
    }
}
