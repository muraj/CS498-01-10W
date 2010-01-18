package dpns_bot;

import battlecode.common.Clock;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
class Bot {
	protected RobotController rc;
    public Bot(RobotController rc) { this.rc = rc; }
    public void AI() { System.out.println("I'm a bot!"); }
	public void yield(){ rc.yield(); }
}
  
class ArchonBot extends Bot {
	public ArchonBot(RobotController rc) { super(rc); }
    public void AI() {
		System.out.println("I'm an Archon!");
    }
}
class AuraBot extends Bot {
	public AuraBot(RobotController rc) { super(rc); }
    public void AI() {
		System.out.println("I'm an Aura!");
    }
}
class ChainerBot extends Bot {
	public ChainerBot(RobotController rc) { super(rc); }
    public void AI() {
		System.out.println("I'm an Chainer!");
    }
}
class CommBot extends Bot {
	public CommBot(RobotController rc) { super(rc); }
    public void AI() {
		System.out.println("I'm an Comm!");
    }
}
class SoldierBot extends Bot {
	public SoldierBot(RobotController rc) { super(rc); }
    public void AI() {
		System.out.println("I'm an Soldier!");
    }
}
class TeleBot extends Bot {
	public TeleBot(RobotController rc) { super(rc); }
    public void AI() {
		System.out.println("I'm an Tele!");
    }
}
class TurretBot extends Bot {
	public TurretBot(RobotController rc) { super(rc); }
    public void AI() {
		System.out.println("I'm an Turret!");
    }
}
class WoutBot extends Bot {
	public WoutBot(RobotController rc) { super(rc); }
    public void AI() {
		System.out.println("I'm an Wout!");
    }
}

public class RobotPlayer implements Runnable {
	private Bot b;
    public RobotPlayer(RobotController rc) throws Exception{
		switch(rc.getRobotType()) {
            case ARCHON:        b = new ArchonBot(rc);  break;
            case AURA:          b = new AuraBot(rc);    break;
            case CHAINER:       b = new ChainerBot(rc); break;
            case COMM:          b = new CommBot(rc);    break;
            case SOLDIER:       b = new SoldierBot(rc); break;
            case TELEPORTER:    b = new TeleBot(rc);    break;
            case TURRET:        b = new TurretBot(rc);  break;
            case WOUT:          b = new WoutBot(rc);    break;
            default:
                throw new Exception("Robot Type not supported yet.");
          }
    }

    public void run() {
		while(true){
			try{
				b.AI();
			}catch(Exception e){
				System.out.println("!! Caught Exception !!");
				e.printStackTrace();
			}
			b.yield();
		}
    }
}
