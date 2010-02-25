package team276;

import battlecode.common.*;

public class Debugger {
    public static final void debugPrint(String str) {
        System.out.println(str);
    }

    public static final void debugPrintBCUsed() {
        System.out.println("BC Used: " + Clock.getBytecodeNum());
    }

    public static final void debugPrintTotalBCUsed() {
        System.out.println("Total BC Used: " + Clock.getBytecodeNum());
    }

    public static final void debugPrintEnergon(RobotController rc) {
        System.out.println("Energon: " + rc.getEnergonLevel());
    }

    public static final void debugSetCounter(Bot b) {
        b.bcCounterStart = Clock.getBytecodeNum();
    }

    public static final void debugPrintCounter(Bot b) {
        System.out.println("Total executed bytecodes:"+ (Clock.getBytecodeNum() - b.bcCounterStart));
    }
}
