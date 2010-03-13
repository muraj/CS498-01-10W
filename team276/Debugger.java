package team276;

import battlecode.common.*;

public class Debugger {
    public static void debug_Print(String str) {
        System.out.println(str);
    }

    public static void debug_PrintBCUsed() {
        System.out.println("BC Used: " + Clock.getBytecodeNum());
    }

    public static void debug_PrintTotalBCUsed() {
        System.out.println("Total BC Used: " + Clock.getBytecodeNum());
    }

    public static void debug_PrintEnergon(RobotController rc) {
        System.out.println("Energon: " + rc.getEnergonLevel());
    }

    public static void debug_SetCounter(Bot b) {
        b.bcCounterStart = Clock.getBytecodeNum();
    }

    public static void debug_PrintCounter(Bot b) {
        System.out.println("Total executed bytecodes:"+ (Clock.getBytecodeNum() - b.bcCounterStart));
    }
}
