import java.io.*;
import java.net.*;
import java.util.ArrayList;

import lejos.hardware.lcd.LCD;
import lejos.robotics.geometry.Line;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.utility.Delay;

class TCPClient {
	public static ArrayList<Line> lines;
	public static Pose position;
	public static Waypoint finish;
	public static float conversionFactor;
	public boolean imageUpdated = false;
	/*
	 * public static void main(String argv[]) throws Exception { String
	 * sentence; String modifiedSentence; Socket clientSocket = new
	 * Socket("10.0.1.3", 2005); System.out.println("Client started");
	 * 
	 * DataOutputStream outToServer = new
	 * DataOutputStream(clientSocket.getOutputStream());
	 * 
	 * BufferedReader inFromServer = new BufferedReader(new
	 * InputStreamReader(clientSocket.getInputStream())); sentence =
	 * "valerie en hello world"; System.out.println(sentence);
	 * outToServer.writeBytes(sentence + '\n'); modifiedSentence =
	 * inFromServer.readLine(); //LCD.drawString("En we zin weg eh", 0, 1);
	 * lines = getLines(modifiedSentence); //System.out.println("FROM SERVER: "
	 * + modifiedSentence); clientSocket.close(); }
	 */
	public TCPClient(String type) {
		try {
			Ask(type);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		/*if (type.equals("map")) {
			try {
				NewMap();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (type.equals("position")) {
			try {
				NewPosition();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/

	}

	@SuppressWarnings("null")
	public static ArrayList<Line> getStringtoLines(String input) {
		String[] arr = input.split(" ");
		ArrayList<Line> lines = new ArrayList<Line>();
		int counter = 0;
		int[] points = new int[4];
		for (String ss : arr) {
			// System.out.println(counter);
			if (counter == 3) {
				counter = 0;
				points[3] = new Integer(ss);
				Line line = new Line(points[0], points[1], points[2], points[3]);
				lines.add(line);
				// System.out.println("[" + points[0] + "," + points[1] + "],["
				// + points[2] + "," + points[3] + "]");
				points = new int[4];
			} else {
				points[counter] = new Integer(ss);
				counter++;

			}

		}
		return lines;

	}

	public Pose getStringtoPosition(String input) {
		String[] arr = input.split(" ");
		System.out.println("input:"+ input);
		System.out.println("arr:"+arr.toString());
		
		position = new Pose(Float.parseFloat(arr[0]), Float.parseFloat(arr[1]), Float.parseFloat(arr[2]));
		//conversionFactor = Float.parseFloat(arr[3]);
		//System.out.println("conversionfactor: "+conversionFactor);
		return position;

	}
	public static Waypoint getStringtoFinish(String input) {
		String[] arr = input.split(" ");
		// System.out.println(arr.toString());
		Waypoint finish = new Waypoint(Float.parseFloat(arr[0]), Float.parseFloat(arr[1]));
		return finish;

	}

	public ArrayList<Line> getLines() {
		return TCPClient.lines;
	}

	public Pose getPosition() {
		return TCPClient.position;
	}
	
	public Waypoint getFinish () {
		return TCPClient.finish;
	}
	
	public float getConversionFactor(){
		return TCPClient.conversionFactor;
	}

	/*public void NewMap() throws UnknownHostException, IOException {
		String sentence;
		String modifiedSentence;
		Socket clientSocket = new Socket("10.0.1.3", 2006);
		System.out.println("Client started");

		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		Delay.msDelay(2000);
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		sentence = "map";
		System.out.println("GET " + sentence);
		outToServer.writeBytes(sentence + '\n');
		System.out.println("Request Map sent");
		modifiedSentence = inFromServer.readLine();

		lines = getStringtoLines(modifiedSentence);
		// System.out.println("FROM SERVER: " + modifiedSentence);
		clientSocket.close();
	}

	public void NewPosition() throws UnknownHostException, IOException {
		String sentence;
		String modifiedSentence;
		Socket clientSocket = new Socket("10.0.1.3", 2006);
		System.out.println("Client started");

		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		Delay.msDelay(2000);
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		sentence = "location";
		System.out.println("GET " + sentence);
		outToServer.writeBytes(sentence + '\n');
		System.out.println("Request Location sent");

		modifiedSentence = inFromServer.readLine();
		// LCD.drawString("En we zin weg eh", 0, 1);
		System.out.println("position:" + modifiedSentence);
		position = getStringtoPosition(modifiedSentence);

		clientSocket.close();
	}*/

	public void Ask(String type) throws UnknownHostException, IOException {
		String sentence;
		String modifiedSentence;
		Socket clientSocket = new Socket("10.0.1.6", 2006);
		System.out.println("Client started");

		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		// Delay.msDelay(2000);
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		// sentence = "location";
		System.out.println("GET " + type);
		outToServer.writeBytes(type + '\n');
		System.out.println("Request " + type + " sent");

		modifiedSentence = inFromServer.readLine();
		// LCD.drawString("En we zin weg eh", 0, 1);
		System.out.println(type + ": " + modifiedSentence+" received");
		if (type.equals("location")) {
			getStringtoPosition(modifiedSentence);
		} else if (type.equals("map")) {
			lines = getStringtoLines(modifiedSentence);
		} else if(type.equals("finish")){
			finish = getStringtoFinish(modifiedSentence);
		}else if(type.equals("conversionFactor")){
			conversionFactor = Float.parseFloat(modifiedSentence);
		}else if (type.equals("updateImage")){
			imageUpdated = Boolean.parseBoolean(modifiedSentence);
		}else if (type.equals("updateLocation")){
			getStringtoPosition(modifiedSentence);
		}
			
		
		clientSocket.close();
	}
}