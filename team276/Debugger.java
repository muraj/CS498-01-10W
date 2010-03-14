package team276;

import battlecode.common.Clock;
import battlecode.common.RobotController;

public class Debugger {
    public static void debug_print(String str) {
        System.out.println(str);
    }

    public static void debug_print_bc_used() {
        System.out.println("BC Used: " + Clock.getBytecodeNum());
    }

    public static void debug_print_total_bc_used() {
        System.out.println("Total BC Used: " + Clock.getBytecodeNum());
    }

    public static void debug_print_energon(RobotController rc) {
        System.out.println("Energon: " + rc.getEnergonLevel());
    }

    public static void debug_set_counter(Bot b) {
        b.bcCounterStart = Clock.getBytecodeNum();
    }

    public static void debug_print_counter(Bot b) {
        System.out.println("Total executed bytecodes:"+ (Clock.getBytecodeNum() - b.bcCounterStart));
    }
}
