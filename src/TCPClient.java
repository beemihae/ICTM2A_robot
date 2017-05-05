
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import lejos.hardware.lcd.LCD;
import lejos.robotics.geometry.Line;
import lejos.robotics.navigation.Pose;
import lejos.utility.Delay;

class TCPClient {
	public static ArrayList<Line> lines;
	public static Pose position;

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
		if (type.equals("map")) {
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
		}

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
				//System.out.println("[" + points[0] + "," + points[1] + "],[" + points[2] + "," + points[3] + "]");
				points = new int[4];
			} else {
				points[counter] = new Integer(ss);
				counter++;

			}

		}
		return lines;

	}

	public static Pose getStringtoPosition(String input) {
		String[] arr = input.split(" ");
		System.out.println(arr.toString());
		Pose position = new Pose(Float.parseFloat(arr[0]), Float.parseFloat(arr[1]), Float.parseFloat(arr[2]));
		return position;

	}

	public ArrayList<Line> getLines() {
		return TCPClient.lines;
	}

	public Pose getPosition() {
		return TCPClient.position;
	}

	public void NewMap() throws UnknownHostException, IOException {
		String sentence;
		String modifiedSentence;
		Socket clientSocket = new Socket("10.0.1.3", 2006);
		System.out.println("Client started");

		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		Delay.msDelay(2000);
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		sentence = "map";
		System.out.println(sentence);
		outToServer.writeBytes(sentence + '\n');

		modifiedSentence = inFromServer.readLine();
		// LCD.drawString("En we zin weg eh", 0, 1);
		System.out.println("points:" + modifiedSentence);
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
		System.out.println(sentence);
		outToServer.writeBytes(sentence + '\n');

		modifiedSentence = inFromServer.readLine();
		// LCD.drawString("En we zin weg eh", 0, 1);
		System.out.println("position:" + modifiedSentence);
		position = getStringtoPosition(modifiedSentence);
		// System.out.println("FROM SERVER: " + modifiedSentence);
		clientSocket.close();
	}
}