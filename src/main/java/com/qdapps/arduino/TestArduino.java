package com.qdapps.arduino;

import cc.arduino.Arduino;

public class TestArduino {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args!= null && args.length > 0){
			System.out.println("Using iName: " + args[0]);
			
		}
		TestArduino ta = new TestArduino();
		ta.iname = args[0];
		ta.start();

	}

	private String iname = null;
	
	private void start() {

		String[] l = Arduino.list();
		if (l!=null && l.length>0){
			for (String s : l)
			System.out.println(s);
		}else{
			System.out.println("list() return empty.");
		}

		// Modify this line, by changing the "0" to the index of the serial
		// port corresponding to your Arduino board (as it appears in the list
		// printed by the line above).
		Arduino arduino = new Arduino(null, iname==null? Arduino.list()[0]:iname, 57600);

		// Alternatively, use the name of the serial port corresponding to your
		// Arduino (in double-quotes), as in the following line.
		// arduino = new Arduino(this, "/dev/tty.usbmodem621", 57600);

		// Set the Arduino digital pins as outputs.
		for (int i = 0; i <= 13; i++)
			arduino.pinMode(i, Arduino.OUTPUT);
		int i = 0;
		while (i < 15){
			try{
				Thread.sleep(1000);
				i++;
				arduino.digitalWrite(13, i%2 == 0 ? Arduino.LOW: Arduino.HIGH);
				//arduino.
			}catch(Exception e){
				
			}
			
		}
		
		arduino.dispose();
	}

}
