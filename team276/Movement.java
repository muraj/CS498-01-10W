package team276;

import battlecode.common.*;

public class Movement {
    protected MapLocation target;

    public Movement(MapLocation target) {
        this.target = target;
    }

    public Movement(int x, int y) {
        this.target = new MapLocation(x, y);
    }
}
