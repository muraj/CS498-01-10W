package team276;

import battlecode.common.*;

public class Debugger {
    public static void debugPrint(String str) {
        System.out.println(str);
    }

    public static void debugPrintBCUsed() {
        System.out.println("BC Used: " + Clock.getBytecodeNum());
    }

    public static void debugPrintTotalBCUsed() {
        System.out.println("Total BC Used: " + Clock.getBytecodeNum());
    }

    public static void debugPrintEnergon(RobotController rc) {
        System.out.println("Energon: " + rc.getEnergonLevel());
    }

    public static void debugSetCounter(Bot b) {
        b.bcCounterStart = Clock.getBytecodeNum();
    }

    public static void debugPrintCounter(Bot b) {
        System.out.println("Total executed bytecodes:"+ (Clock.getBytecodeNum() - b.bcCounterStart));
    }
}
