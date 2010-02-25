package team276;

import battlecode.common.*;

public class Debugger {
    public static void debug_print(String str) {
        System.out.println(str);
    }

    public static void debug_printBCUsed() {
        System.out.println("BC Used: " + Clock.getBytecodeNum());
    }

    public static void debug_printTotalBCUsed() {
        System.out.println("Total BC Used: " + Clock.getBytecodeNum());
    }

    public static void debug_printEnergon(RobotController rc) {
        System.out.println("Energon: " + rc.getEnergonLevel());
    }

    public static void debug_setCounter(Bot b) {
        System.out.println("Setting counter..");
        b.bcRoundCounterStart = Clock.getRoundNum();
        b.bcCounterStart = Clock.getBytecodeNum();
    }

    public static void debug_printCounter(Bot b) {
        System.out.println("Counter: Executed bytecodes: " + (((Clock.getBytecodeNum() - b.bcCounterStart)) + ((Clock.getRoundNum() - b.bcRoundCounterStart) * GameConstants.BYTECODES_PER_ROUND)));
    }
}
