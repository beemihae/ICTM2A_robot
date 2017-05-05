import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.MirrorMotor;
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
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
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
	static float gridSpace;
	static float clearance = 1; // moet nog aangepast worden
	static SearchAlgorithm alg = new AstarSearchAlgorithm();
	static Pose currentPose = new Pose(854.38495f, 1601.0002f, 0f);
	static Waypoint goal;
	static IRSensor sensor;
	static boolean destinationReached = false;
	static int maxdistance;
	static boolean Objectdetected;
	

	public static void main(String[] args) {
		createPilot();
		System.out.println("Pilot created");
		pilot.setLinearSpeed(1000);
		pilot.setAngularSpeed(1000);
		// pilot.rotate(30);

		// float[][] boundingPoints = new
		// float[][]{{11f,5f},{105f,5f},{105f,115f},{11f,115f}};
		//ArrayList<float[][]> contouren = new ArrayList<float[][]>();
		//contouren.add(new float[][] { { 175f, 200f }, { 300f, 225f }, { 250f, 325f }, { 100f, 225f } });
		
		goal = new Waypoint(new lejos.robotics.geometry.Point(1000f, 300f));
		
		createNavigator();
		updatePose();
		System.out.println("Navigator created");
		
		//updateMap(1152f, 2289f);
		//updateMesh();
		
		try {
			updatePath(1152f, 2289f);
			System.out.println("Path updated");
		} catch (DestinationUnreachableException e) {
			System.out.println("Destination of robot is unreachable");
		}

		kapitein.setPath(currentPath);

		//float[][] points = new float[currentPath.size()][2];
		//for (int i = 0; i < points.length; i++) {
		//	points[i] = new float[] { currentPath.get(i).x, currentPath.get(i).y };
		//}
		//Line[] pathLines = new Line[points.length - 1];
		//for (int i = 0; i < pathLines.length; i++) {
		//	pathLines[i] = new Line(points[i][0], points[i][1], points[i + 1][0], points[i + 1][1]);
		//}
		//LineMap paddd = new LineMap(pathLines, new Rectangle(0, 0, 1152, 2289));
		//try {
		//	paddd.createSVGFile("path.svg");
		//} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}

		sensor = new IRSensor();
		Behavior b1 = new DoPath();
		// Behavior b2 = new DetectObstacle();
		
		Arbitrator arbitrator = new Arbitrator(new Behavior[] { b1 });
		arbitrator.go();

	}
}
		public void SensorRun(){
		
		}

		static ArrayList<float[]> getWaypoints() { /**
												 * geeft Arraylist van [x,y]
												 * coordinaten van waypoints van
												 * current path
												 **/
		ArrayList<float[]> waypoints = new ArrayList<float[]>();
		for (Waypoint waypoint : currentPath) {
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
		Wheel leftwheel = WheeledChassis.modelWheel(Motor.B, 54.8).invert(true).offset(-76);
		Wheel rightwheel = WheeledChassis.modelWheel(Motor.C, 54.8).invert(true).offset(76);
		chassis = new WheeledChassis(new Wheel[] { leftwheel, rightwheel }, WheeledChassis.TYPE_DIFFERENTIAL);
		pilot = new RobotPilot(chassis, Motor.A);
		pilot.setAngularSpeed(300);
		pilot.setLinearSpeed(1000);
		//pilot.setMinRadius(radius);
	}

	static void createNavigator() {
		kapitein = new Navigator(pilot);
	}

	static void updatePose() {
		// moet aangevuld worden met code van imageprocessing
		TCPClient Pose = new TCPClient("position");
		currentPose = Pose.getPosition();
		System.out.println(currentPose.toString());
		kapitein.getPoseProvider().setPose(currentPose);
	}

	static void updateMap(float width, float height) {
		ArrayList<Line> lines = new ArrayList<Line>();
		TCPClient client = new TCPClient("map");
		lines = client.getLines();
		/*
		 * for (Iterator<float[][]> iterator = ( contours.iterator());
		 * iterator.hasNext();) { // voor elke contour ArrayList<float[]> points
		 * = new ArrayList<float[]>(); float[][] contour = iterator.next(); for
		 * (int i = 0; i < contour.length; i++) { points.add(new
		 * float[]{contour[i][0], contour[i][1]}); } for (int i = 0; i <
		 * points.size()-1; i++) { lines.add(new Line(points.get(i)[0],
		 * points.get(i)[0], points.get(i+1)[0], points.get(i+1)[1])); }
		 * lines.add(new Line(points.get(0)[0], points.get(0)[1],
		 * points.get(points.size()-1)[0], points.get(points.size()-1)[1])); }
		 */
		/*
		 * for (int i = 0; i < lines.size(); i++) {
		 * System.out.println(lines.get(i).x1);
		 * System.out.println(lines.get(i).y1);
		 * System.out.println(lines.get(i).x2);
		 * System.out.println(lines.get(i).y2); System.out.println("\n");
		 */
		/*try {
			System.out.println("read lines");
			File x = new File("Lines.txt");
			Scanner sc = new Scanner(x);
			int[] points = new int[4];
			int counter = 0;
			while (sc.hasNext()) {

				// System.out.println(counter);
				if (counter == 3) {
					counter = 0;
					points[3] = new Integer(sc.nextInt());
					Line line = new Line(points[0], points[1], points[2], points[3]);
					lines.add(line);
					System.out.println("[" + points[0] + "," + points[1] + "],[" + points[2] + "," + points[3] + "]");
					points = new int[4];
				} else {
					points[counter] = sc.nextInt();
					counter++;

				}

			}
			System.out.println("read lines");
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}*/
		currentMap = new LineMap(lines.toArray(new Line[lines.size()]), new Rectangle(0, 0, width, height));

		try {
			currentMap.createSVGFile("map.svg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;

		gridSpace = (width + height) / 20;
		// updatePose();
	}

	static void updateMesh() {
		currentMesh = new FourWayGridMesh(currentMap, gridSpace, clearance);
	}

	static void updatePath() throws DestinationUnreachableException {
		NodePathFinder padvinder = new NodePathFinder(alg, currentMesh);
		currentPath = padvinder.findRoute(currentPose, goal);
	}

	static void updatePath(float width, float height)
			throws DestinationUnreachableException {
		updatePose();
		updateMap(width, height);
		updateMesh();
		updatePath();
	}

	

class RobotPilot extends MovePilot {
	RegulatedMotor sensorMotor;
	RotationListener listener = new RotationListener();

	public RobotPilot(Chassis chassis, RegulatedMotor sensorMotor) {
		super(chassis);
		this.sensorMotor = sensorMotor;
		super.addMoveListener(listener);
	}

	public void rotateSensor(int angle) {
		sensorMotor.rotate(angle);
	}

	class RotationListener implements MoveListener {
		private int angle = 45; // sensor zal over 45 graden draaien
		float sign;

		public void moveStarted(Move event, MoveProvider mp) {
			if (event.getMoveType().equals(Move.MoveType.ROTATE)) {
				sign = Math.signum(event.getAngleTurned());
				rotateSensor((int) (sign * angle));
			} else if (event.getMoveType().equals(Move.MoveType.ARC)) {
				//sign = Math.signum(event.getArcRadius());
				//rotateSensor((int) (sign * angle));
				angle = 0;
			}
			;
		}

		public void moveStopped(Move event, MoveProvider mp) {
			rotateSensor(-(int) (sign * angle));
		}
	}
}

class DoPath implements Behavior {

	public void action() {
		while (!Pilot.kapitein.pathCompleted()) {
			Pilot.kapitein.followPath();
			Thread.yield();
		}
		System.out.println("Destination reached");
		Pilot.kapitein.clearPath();
		Pilot.destinationReached = true;
		//suppress();
	}

	public void suppress() {

	}

	public boolean takeControl() {
		return !Pilot.destinationReached;
	}

}

class DetectObstacle implements Behavior {

	public void action() {
		// take new picture and get new bounding points en contouren
		// make new image with detected object
		// Pilot.updateMap(width, height, contours);
		// ...

		/*
		 * try { //Pilot.updatePath(); met code van imageProcessing } catch
		 * (DestinationUnreachableException e) {
		 * 
		 * }
		 */

		System.out.println("Object Detected");
	}

	public void suppress() { // will never be called
	}

	public boolean takeControl() {
		return Pilot.sensor.distance < 3; // minder dan 50mm van object
	}

}

class IRSensor extends Thread {
	EV3IRSensor ir = new EV3IRSensor(SensorPort.S1);
	SampleProvider sp = ir.getDistanceMode();
	public int distance;

	IRSensor() {

	}

	public void run() {
		while (true) {
			float[] sample = new float[sp.sampleSize()];
			sp.fetchSample(sample, 0);
			distance = (int) sample[0];
			// System.out.println(" Distance: " + distance);

		}

	}

}
