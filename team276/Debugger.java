package team276;

import battlecode.common.*;

public class Debugger {
    public static void debug_print(String str) {
        System.out.println(str);
    }

    public static void debug_printBCUsed() {
        System.out.println("BC Used: " + Clock.getBytecodeNum());
    }

    public static void debug_printEnergon(RobotController rc) {
        System.out.println("Energon: " + rc.getEnergonLevel());
    }

    public static void debug_setCounter(Bot b) {
        b.bcCounterStart = Clock.getBytecodeNum();
    }

    public static void debug_printCounter(Bot b) {
        System.out.println("Total executed bytecodes:"+ (Clock.getBytecodeNum() - b.bcCounterStart));
    }
}
