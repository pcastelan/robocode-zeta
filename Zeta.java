package salesiano;


import robocode.*;

import java.awt.*;
// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * Zeta - a robot by (paula)
 */
public class Zeta extends AdvancedRobot
{

	int count = 0; // Keeps track of how long we've
	// been searching for our target
	double gunTurnAmt; // How much to turn our gun when searching
	String trackName; // Name of the robot we're currently tracking
	
	/**
	 * 1: team
	 * 2: vs
	 */
	int mode = 2; 
	int tracked = 0;  // number of enemies found	

	/**
	 * run: Zeta's default behavior
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.darkGray);
		setGunColor(new Color(50, 50, 20));
		setRadarColor(new Color(75, 0, 130));
		setScanColor(Color.white);
		setBulletColor(Color.white);
		
		out.println("teste");
		// Prepare gun
		trackName = null; // Initialize to not tracking anyone
		setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
		gunTurnAmt = 10; // Initialize gunTurn to 10
		while(true) {
		// turn the Gun (looks for enemy)
			setTurnGunRight(gunTurnAmt);
			
			// Keep track of how long we've been looking
			count++;
			// If we've haven't seen our target for 2 turns, look left
			if (count > 2) {
				gunTurnAmt = -10;
			}
			// If we still haven't seen our target for 5 turns, look right
			if (count > 5) {
				gunTurnAmt = 10;
			}
			// If we *still* haven't seen our target after 10 turns, find another target
			if (count > 11) {
				trackName = null;
			}
			execute();
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		String found;

		// If we have a target, and this isn't it, return immediately
			// so we can get more ScannedRobotEvents.
		if (trackName != null && !e.getName().equals(trackName)) {
			return;
		}
		
		found = e.getName(); // get name of new found robot
		setFire(2);
		execute();
		// If we don't have a target, well, now we do!
		if (trackName == null) {
			trackName = found;
			out.println("Tracking " + trackName);
			
		} else if(trackName!= found) {
			// if a second robot is found change game mode
			mode = 1;
		}
		
		// This is our target.  Reset count (see the run method)
		count = 0;
		// If our target is too far away, turn and move toward it.
		if (e.getDistance() > 150) {
			gunTurnAmt = normalRelativeAngleDegreesAlt(e.getBearing() + (getHeading() - getRadarHeading()));

			setTurnGunRight(gunTurnAmt); // Try changing these to setTurnGunRight,
			setTurnRight(e.getBearing()); // and see how much Tracker improves...
			// (you'll have to make Tracker an AdvancedRobot)
			setAhead(e.getDistance() - 140);
			execute();
			return;
		}

		// Our target is close.
		gunTurnAmt = normalRelativeAngleDegreesAlt(e.getBearing() + (getHeading() - getRadarHeading()));
		setTurnGunRight(gunTurnAmt);
		setFire(5);
		execute();
		// Our target is too close!  Back up.
		if (e.getDistance() < 100) {
			if (e.getBearing() > -90 && e.getBearing() <= 90) {
				setBack(40);
			} else {
				setAhead(40);
			}
			execute();
		}
		scan();
	}
	
	/**
	 * onHitRobot:  Set him as our new target
	 */
	public void onHitRobot(HitRobotEvent e) {
		// Only print if he's not already our target.
		if (trackName != null && !trackName.equals(e.getName())) {
			out.println("Tracking " + e.getName() + " due to collision");
		}
		// Set the target
		trackName = e.getName();
		// Back up a bit.
		// Note:  We won't get scan events while we're doing this!
		// An AdvancedRobot might use setBack(); execute();
		gunTurnAmt = normalRelativeAngleDegreesAlt(e.getBearing() + (getHeading() - getRadarHeading()));
		setTurnGunRight(gunTurnAmt);
		fire(4);
		setBack(50);
		execute();
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		out.println("hit by bullet");
		setTurnLeft(100);
		setTurnGunRight(120);
		setAhead(100);
		setFire(3);
		out.println("sera");
		execute();	
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
	
	}
	/**
	 * onWin:  Do a victory dance
	 */
	public void onWin(WinEvent e) {
		for (int i = 0; i < 50; i++) {
			turnRight(30);
			turnLeft(30);
		}
	}
	
	public double normalRelativeAngleDegreesAlt(double angle) {
			return (angle %= 360) >= 0 ? (angle < 180) ? angle : angle - 360 : (angle >= -180) ? angle : angle + 360;
		}	
	}
