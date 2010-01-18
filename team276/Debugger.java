package team276;

import battlecode.common.Clock;

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
}
