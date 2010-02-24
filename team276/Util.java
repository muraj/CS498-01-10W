package team276;

import battlecode.common.*;
import java.util.Comparator;

public class Util {
    private final static MapLocation zero = new MapLocation(0,0);
    public static Direction coordToDirection(int dx, int dy) {
        return zero.directionTo(new MapLocation(dx, dy));
    }
    /* Message Utility Functions */
    public static class MessageComparator implements Comparator<Message> {
        public int compare(Message m1, Message m2){
            // retval < 0 => m1 is bigger, retval == 0 => equal
            return m1.getNumBytes() - m2.getNumBytes();
        }
    }
    public enum MsgType {
        BEACON(0), ATTACKER(1);
        public final int value;
        public static final int INIT_TTL = 10;
        MsgType(int val){ value = val; }
    }
    public static final int CHKSEED = 0x5B125AB;  //Some random starting value
    public static int chksum(Message m){
        int ret = CHKSEED;
        if(m.strings != null){
            for(int i=0;i<m.strings.length;i++){
                if(m.strings[i] == null) continue;
                for(int j=0;j<m.strings[i].length(); j++)
                    ret ^= m.strings[i].charAt(j); //Not a 'good' way to do this.
            }
        }
        if(m.ints != null){
            for(int i=0;i<m.ints.length;i++){
                ret ^= m.ints[i];
            }
        }
        return ret ^ m.getNumBytes();   //Not sure if needed.
    }
}
