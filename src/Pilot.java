import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.MirrorMotor;
import lejos.robotics.RangeScanner;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.geometry.Line;
import lejos.robotics.geometry.Rectangle;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.NavigationListener;
//import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.hardware.Button;
import lejos.robotics.pathfinding.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ArrayList.*;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.Cloneable;
import java.lang.Iterable;
import java.util.Collection.*;
import java.util.List.*;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.List.*;


public class Pilot {
	static RobotPilot pilot;
	static WheeledChassis chassis;
	static Path currentPath;
	static Navigator kapitein;
	static PoseProvider ppv;
	static LineMap currentMap;
	static FourWayGridMesh currentMesh;
	static float gridSpace = 50;
	static float clearance = 80; // mm // moet nog aangepast worden
	static SearchAlgorithm alg = new AstarSearchAlgorithm();
	static Pose currentPose;  // = new Pose(854.38495f, 1601.0002f, 0f);
	static Waypoint goal;
	static boolean destinationReached = false;
	static float maxdistance = 100; // cm
	static float conversionFactor;
	static float virtualLineLength = 100; // mm // lengte van virtuele lijn die
											// hij toevoegt bij object detection
											// via sensor
	static boolean objectDetected;
	static ArrayList<Line> lines = new ArrayList<Line>();
	static ArrayList<Line> detectedLines = new ArrayList<Line>();
	static float width = 1080f;
	static float height = 1920f;
	static boolean voorObstakel = false;
	static long msWaitTime = 5000;
	static float maxError = 1000f;  //max error from expected location

	public static void main(String[] args) {
		
		
		// pilot.rotate(30);

		// float[][] boundingPoints = new
		// float[][]{{11f,5f},{105f,5f},{105f,115f},{11f,115f}};
		// ArrayList<float[][]> contouren = new ArrayList<float[][]>();
		// contouren.add(new float[][] { { 175f, 200f }, { 300f, 225f }, { 250f,
		// 325f }, { 100f, 225f } });

		//goal = new Waypoint(new lejos.robotics.geometry.Point(1000f, 300f));
		
		reDoImageProcessing();
		updateConversionFactorFromClient();
		clearance = clearance * conversionFactor;
		virtualLineLength = virtualLineLength * conversionFactor;
		createPilot();
		System.out.println("Pilot created");
		updateFinishFromClient();
		createNavigator();
		updatePoseFromClient();

		
		
		

		// updateMap(1152f, 2289f);
		// updateMesh();

		try {
			updatePath(width, height, false);
			System.out.println("Path updated");
		} catch (DestinationUnreachableException e) {
			System.out.println("Destination of robot is unreachable");
		}

		kapitein.setPath(currentPath);

		 float[][] points = new float[currentPath.size()][2];
		 for (int i = 0; i < points.length; i++) {
		 points[i] = new float[] { currentPath.get(i).x, currentPath.get(i).y
		 };
		 }
		 Line[] pathLines = new Line[points.length - 1];
		 for (int i = 0; i < pathLines.length; i++) {
		 pathLines[i] = new Line(points[i][0], points[i][1], points[i + 1][0],
		 points[i + 1][1]);
		 }
		 LineMap paddd = new LineMap(pathLines, new Rectangle(0, 0, 1152, 2289));
		 try {
		 paddd.createSVGFile("path.svg");
		 } catch (IOException e) {
		 //TODO Auto-generated catch block
		 e.printStackTrace();
		}
		Drive driving = new Drive();
		driving.start();

	}

	static ArrayList<float[]> getWaypoints() { /**
												 * geeft Arraylist van [x,y]
												 * coordinaten van waypoints van
												 * current path
												 **/
		ArrayList<float[]> waypoints = new ArrayList<float[]>();
		for (Waypoint waypoint : kapitein.getPath()) {
			waypoints.add(new float[] { waypoint.x, waypoint.y });
		}
		/*
		 * for (int i = 0; i<waypoints.size(); i++) {
		 * System.out.println("x"+i+" = "+waypoints.get(i)[0]);
		 * System.out.println("y"+i+" = "+waypoints.get(i)[1]+"\n"); }
		 */
		return waypoints;
	}

	static void createPilot() {
		Wheel leftwheel = WheeledChassis.modelWheel(Motor.B, 54.8*conversionFactor).invert(true).offset(-76*conversionFactor);
		Wheel rightwheel = WheeledChassis.modelWheel(Motor.C, 54.8*conversionFactor).invert(true).offset(76*conversionFactor);
		chassis = new WheeledChassis(new Wheel[] { leftwheel, rightwheel }, WheeledChassis.TYPE_DIFFERENTIAL);
		pilot = new RobotPilot(chassis, Motor.A);
		pilot.setAngularSpeed(90);
		pilot.setLinearSpeed(90);
		pilot.setAngularAcceleration(110);
		pilot.setLinearAcceleration(110);
		//pilot.setMinRadius(radius);
	}

	static void createNavigator() {
		kapitein = new Navigator(pilot);
	}
	
	

	static void updatePoseFromClient() {
		TCPClient Pose = new TCPClient("location");
		currentPose = Pose.getPosition();
		kapitein.getPoseProvider().setPose(currentPose);
		System.out.println(currentPose.toString());
		kapitein.getPoseProvider().setPose(currentPose);
	}
	
	static Pose getNewPoseFromClient () {
		TCPClient Pose = new TCPClient("updateLocation");
		return Pose.getPosition();
	}
	
	static void updateFinishFromClient() {
		TCPClient Finish = new TCPClient ("finish");
		goal = Finish.getFinish();
		System.out.println("Finish:"+goal);
	}
	
	static void updateConversionFactorFromClient() {
		TCPClient Cf = new TCPClient("conversionFactor");
		conversionFactor = Cf.getConversionFactor();
	}

	static void updateMap(float width, float height, boolean addLine) {
		// indien addLine true is gaat er virtueel een lijn bijgeplaatst worden op de map
		// indien addLine false is wordt een nieuwe map gevraagd. (om image te updaten: zet eerst new TCPClient("updateImage"); )
		
		if (!addLine) {
		TCPClient client = new TCPClient("map");
		lines = client.getLines();
		}

		if (addLine) {
			double alpha = (Pilot.currentPose.getHeading() + Pilot.pilot.sensorMotor.getTachoCount()) * Math.PI / 180;
			float x = Pilot.currentPose.getX();
			float y = Pilot.currentPose.getY();
			float cosa = (float) Math.cos(alpha);
			float sina = (float) Math.sin(alpha);
			detectedLines.add(new Line(x + (maxdistance /2 * 10 - virtualLineLength / 2) * cosa,
					y + (maxdistance /2 * 10 + virtualLineLength / 2) * sina,
					x + (maxdistance /2 * 10 + virtualLineLength / 2) * cosa,
					y + (maxdistance /2 * 10 - virtualLineLength / 2) * sina));
			// geef eventueel object detectedLines door aan server

		}
		lines.addAll(detectedLines);
		
		currentMap = new LineMap(lines.toArray(new Line[lines.size()]), new Rectangle(0, 0, width, height));
		
		try {
			currentMap.createSVGFile("map.svg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;

		kapitein.allowableRemoveDistance=gridSpace*1.05f;
		// updatePose();
	}

	static void updateMesh() {
		currentMesh = new FourWayGridMesh(currentMap, gridSpace, clearance);
	}

	static void updatePath() throws DestinationUnreachableException {
		NodePathFinder padvinder = new NodePathFinder(alg, currentMesh);
		currentPath = padvinder.findRoute(currentPose, goal);
		kapitein.setPath(currentPath);
		System.out.println("Path updated");
	}
	static void updatePath(float width, float height, boolean detected) throws DestinationUnreachableException {
		if (!detected)
		updatePoseFromClient();
		else {
			currentPose = getNewPoseFromClient();
			kapitein.getPoseProvider().setPose(currentPose);
		}
		updateMap(width, height, detected);
		updateMesh();
		updatePath();
		kapitein.addNavigationListener(new NavListener());
	}

	static void reDoImageProcessing() {
		new TCPClient("updateImage");
	}
}

class RobotPilot extends MovePilot {
	static RegulatedMotor sensorMotor;
	static RotationListener listener = new RotationListener();

	public RobotPilot(Chassis chassis, RegulatedMotor sensorMotor) {
		super(chassis);
		RobotPilot.sensorMotor = sensorMotor;
		super.addMoveListener(listener);
	}

	public static void rotateSensor(int angle) {
		sensorMotor.rotate(-angle);
	}
	
	public void rotate (double angle) {
		super.rotate(-angle);		
	}

	static class RotationListener implements MoveListener {
		// private static int angle = 45; // sensor zal over 45 graden draaien
		// float sign;
		// static long movetime;
		// static long starttime;
		// static long stoptime;
		int angle;
		public void moveStarted(Move event, MoveProvider mp) {
			
			if (event.getMoveType().equals(Move.MoveType.ROTATE)) {
				angle = (int) ((event.getAngleTurned()+sensorMotor.getTachoCount())*1.15);  //oorspronkelijk geen tachocount bijgeteld
				if (angle > 90) {
					angle = 90;
				}
				else if (angle<-90) {
					angle = - 90;
				}
				else if (Math.abs(angle) < 5) {
					angle = 0;
				}
				if (angle!=0) {
					try {
					rotateSensor(angle);				//stond oorspronkelijk buiten try
					Thread.sleep(100);
					rotateSensor (-angle);				//stond oorspronkelijk na catch
					} catch (InterruptedException e) {
						sensorMotor.stop(true);			//stond er oorspronkelijk niet bij
						e.printStackTrace();
					}
					
					
					angle = 0;
				}
				
			 
			// starttime = System.currentTimeMillis();
			System.out.println("pilot draaiing: " + event.getAngleTurned());
			
			}
		}

		public void moveStopped(Move event, MoveProvider mp) {
			// stoptime = System.currentTimeMillis();
			// movetime = movetime + stoptime - starttime;
			// System.out.println(movetime);
			
			// Pilot.voorObstakel =
			// (Pilot.voorObstakel)&&(movetime<Pilot.msWaitTime); // als de tijd
			// dat hij moves gedaan heeft sinds detecteren van
			// vorige obstakel groter is dan 1 seconde staat hij er niet meer
			// voor
			if (!event.getMoveType().equals(Move.MoveType.ROTATE)) {
			synchronized (Drive.lockSensor) {
				if (Pilot.voorObstakel) {
					Pilot.voorObstakel = false;
					// movetime = 0; // reset movetime van obstakel
					Drive.lockSensor.notify();
				}
			}
			}
			if (angle!=0) {
				rotateSensor(-angle);
				angle = 0;
			}
		}
	}
}

class NavListener implements NavigationListener {
	@Override
	public void atWaypoint(Waypoint currentWaypoint, Pose pose, int sequence) {
		Pilot.currentPose = Pilot.getNewPoseFromClient();
		
		//if pilot further away than 100mm from expected pose, navigate to expected pose.
		Pilot.kapitein.getPoseProvider().setPose(Pilot.currentPose);
		float distanceFromExpectedPose = Pilot.currentPose.distanceTo(pose.getLocation());
		System.out.println("Distance from expected pose: "+distanceFromExpectedPose);
		if (distanceFromExpectedPose>Pilot.maxError*Pilot.conversionFactor) {
			Pilot.kapitein.addWaypoint(currentWaypoint, true);
			System.out.println("error from location too big, redirecting to expected location");
		}
		
		
		
		/*
		// Rotate sensor to next waypoint
		if (Pilot.kapitein.getPath().size()>0) {
			Waypoint nextWaypoint = Pilot.kapitein.getPath().get(1);
			// calculate angle between next waypoint and current robot position
			double angle = Math.atan2(nextWaypoint.y - currentWaypoint.y, nextWaypoint.x - currentWaypoint.x) * 180
					/ Math.PI - Pilot.currentPose.getHeading();
			// look in direction of next waypoint
			if (angle > 90) {
				angle = 90;
			} else if (angle < -90) {
				angle = -90;
			}
			// System.out.println("sensordraaiing: "+(int)angle);
			if (Math.abs(angle)>10) {
				RobotPilot.rotateSensor((int) (angle));
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			RobotPilot.rotateSensor((int) (-angle));
		}
		
		*/
		 
	}

	public void pathComplete(Waypoint waypoint, Pose pose, int sequence) {
	}

	public void pathInterrupted(Waypoint waypoint, Pose pose, int sequence) {
	}
}

class DoPath implements Behavior {
	IRSensor sensor;

	public void action() {
		System.out.println("Behavior DoPath activated");
		sensor = new IRSensor();
		sensor.start();

		while (!Pilot.destinationReached && !Pilot.objectDetected) {
			Pilot.kapitein.followPath();

			if (Pilot.kapitein.pathCompleted()) {
				System.out.println("Destination reached");
				Pilot.kapitein.clearPath();
				
				Pilot.destinationReached = true;
				Pilot.kapitein.getPoseProvider().setPose(Pilot.getNewPoseFromClient());
				if (Pilot.kapitein.getPoseProvider().getPose().distanceTo(Pilot.goal)>100*Pilot.conversionFactor) {
					Pilot.kapitein.goTo(Pilot.goal);					
				}
				Sound.beepSequenceUp();
				System.exit(0);
			}
		}
		if (Pilot.objectDetected)
			suppress();
	}

	public void suppress() {
		System.out.println("Behavior DoPath suppressed");
		sensor.interrupt();
		Delay.msDelay(10);
		try {
			sensor.ir.close();
		} catch (Exception e) {
		}
		;
		Pilot.voorObstakel = true;

	}

	public boolean takeControl() {
		return !Pilot.destinationReached && !Pilot.objectDetected;
	}

}

class DetectObstacle implements Behavior {

	public void action() {
		System.out.println("Behavior DetectObstacle activated");
		if (Pilot.objectDetected) {
			Pilot.pilot.stop();
			RobotPilot.sensorMotor.stop();
			Pilot.kapitein.clearPath();
			try {
				Pilot.updatePath(Pilot.width, Pilot.height, true); // berekent
																	// nieuw pad
																	// met
																	// virtuele
																	// lijn voor
																	// sensor
				Pilot.objectDetected = false;
			} catch (DestinationUnreachableException e) {
				System.out.println("Destination unreachable, taking new picture");
				Pilot.detectedLines.clear();
				Pilot.reDoImageProcessing();
				Pilot.updateMap(Pilot.width, Pilot.height, false);
				e.printStackTrace();
			}
			System.out.println("Object Detected");
			Pilot.objectDetected = false;
		}
	}

	public void suppress() {
		System.out.println("Behavior DetectObstacle suppressed");
	}

	public boolean takeControl() {
		return Pilot.objectDetected; // minder dan 50mm van object
	}

}

class IRSensor extends Thread {
	EV3IRSensor ir = new EV3IRSensor(SensorPort.S4);
	SampleProvider sp = ir.getDistanceMode();
	public float distance;

	IRSensor() {
	}

	public void run() {
		try {
			synchronized (Drive.lockSensor) {
				while (Pilot.voorObstakel) {
					Drive.lockSensor.wait();
				}
			}

			while (!Pilot.objectDetected) {
				/*
				 * while(Pilot.voorObstakel) { sleep(Pilot.msWaitTime);
				 * Pilot.voorObstakel=false; }
				 */
				// System.out.println("sensing");
				detect();
				sleep(50);
			}
		} catch (InterruptedException e) {
		}

	}

	private void detect() throws InterruptedException {
		float[] sample = new float[sp.sampleSize()];
		sp.fetchSample(sample, 0);
		distance = sample[0];
		// System.out.println(" Distance: " + distance);
		Pilot.objectDetected = distance < Pilot.maxdistance;

	}

}

class Drive extends Thread {
	static final Object lockSensor = new Object();
	Behavior b1 = new DoPath();
	Behavior b2 = new DetectObstacle();
	Arbitrator arbitrator = new Arbitrator(new Behavior[] { b1, b2 });

	public void start() {
		arbitrator.go();
	}
}