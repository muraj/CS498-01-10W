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
        beginUpkeep();
        breakout();
        yield();

        while (true) {
            beginUpkeep();

            // If we've completed a move, try another!
            if(movement == null)
                movement = new Movement(this, currentLocation.getX() + 40, currentLocation.getY());

            handleMovement();
            yield();
        }
    }
}
