package team276;

import battlecode.common.*;

public class ArchonBot extends Bot {
    public ArchonBot(RobotController rc, Team t) {
        super(rc,t);
    }

    private void breakout() throws Exception {
        int dx = 0;
        int dy = 0;

        if(id != 91) {
            rc.suicide();
            yield();
        }
    }

    public void AI() throws Exception {
        breakout();
        yield();

        movement = new Movement(this, currentLocation, currentLocation.getX() + 15, currentLocation.getY() - 15);

        while (true) {
            beginUpkeep();
            yield();
        }
    }
}
