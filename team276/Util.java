package team276;

import battlecode.common.*;

public class Util {
    public static Direction coordToDirection(int dx, int dy) {
        if(dx < 0) {
            if(dy > 0)
                return Direction.NORTH_EAST;
            else if(dy < 0)
                return Direction.SOUTH_EAST;
            else
                return Direction.EAST;
        } else if(dx > 0) {
            if(dy > 0)
                return Direction.NORTH_WEST;
            else if(dy < 0)
                return Direction.SOUTH_WEST;
            else
                return Direction.WEST;
        } else {
            if(dy < 0)
                return Direction.SOUTH;
            else
                return Direction.NORTH;
        }
    }
}
