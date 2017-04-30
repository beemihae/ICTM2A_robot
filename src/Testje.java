
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
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.MoveProvider;
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

import java.util.ArrayList;



 
public class Testje {
	static RobotPilot1 pilot;
	static WheeledChassis chassis;
	//static ImageProcessorJRE7 imageProcessor;
	static void createPilot () {
		Wheel leftwheel = WheeledChassis.modelWheel(Motor.B, 54.8).invert(true).offset(76);
		Wheel rightwheel = WheeledChassis.modelWheel(Motor.C, 54.8).invert(true).offset(-76);
		chassis = new WheeledChassis(new Wheel[] {leftwheel,  rightwheel}, WheeledChassis.TYPE_DIFFERENTIAL);
		pilot = new RobotPilot1(chassis, Motor.A);
	}
	
	public static void main(String[] args) {
		//imageProcessor = new ImageProcessorJRE7();
		 createPilot();
		 pilot.setLinearSpeed(100);
		 pilot.setAngularSpeed(100);
		 pilot.rotate(-30);
		 float[][] boundingPoints = new float[][]{{11f,5f},{105f,5f},{105f,115f},{11f,115f}};
		 ArrayList<float[][]> contouren = new ArrayList<float[][]>();
		 contouren.add(new float[][]{{35f, 40f},{60f, 45f},{50f, 65f},{20f,50f}});
		 //pilot.travel(600);
		 //pilot.arc(200, 90);
		 //Behavior b1 = new Drive();
		 //Arbitrator arbitrator = new Arbitrator (new Behavior[] {b1});
		 //arbitrator.go();
	    
	  }
}

class RobotPilot1 extends MovePilot {
	RegulatedMotor sensorMotor;
	RotationListener listener = new RotationListener();
	public RobotPilot1(Chassis chassis, RegulatedMotor sensorMotor) {
		super(chassis);
		this.sensorMotor=sensorMotor;
		super.addMoveListener(listener);
	}
	public void rotateSensor(int angle) {
		sensorMotor.rotate(angle);
	}
	
	class RotationListener implements MoveListener {
		int angleArc=45;        // sensor zal over 45 draaien bij arc
		int angleRotation=75;   // sensor zal over 75 draaien bij rotation
		float sign;
		int angle;
		public void moveStarted(Move event, MoveProvider mp) {
			if (event.getMoveType().equals(Move.MoveType.ROTATE)){
				sign = Math.signum(event.getAngleTurned());
				rotateSensor((int)(sign*angleRotation));
				angle=angleRotation;
			}
			else if (event.getMoveType().equals(Move.MoveType.ARC)){
				sign = Math.signum(event.getArcRadius());
				rotateSensor((int)(sign*angleArc));
				angle=angleArc;
			}			
		}
		public void moveStopped(Move event, MoveProvider mp) {
			rotateSensor(-(int)(sign*angle));;			
		}
	}
	
}
	
	

/*class Drive implements Behavior {
	private boolean _surpressed = false;
	
	public void action() {
		_surpressed = false;
		while (!_surpressed) {
			Testje.pilot.arcForward(100);
		}
	}
	
	public void suppress() {
		this._surpressed=true;
		
	}
	
	public boolean takeControl() {
		return true;
	}

}

class IRSensor1 extends Thread {
    EV3IRSensor ir = new EV3IRSensor(SensorPort.S1);
    SampleProvider sp = ir.getDistanceMode();
    public int distance = 255;

    IRSensor1()
    {

    }
    
    public void run()
    {
        while (true)
        {
            float [] sample = new float[sp.sampleSize()];
            sp.fetchSample(sample, 0);
            distance = (int)sample[0];
            System.out.println(" Distance: " + distance);
            
        }
        
    }
    
    
}
*/