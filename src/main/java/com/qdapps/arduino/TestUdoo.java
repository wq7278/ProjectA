package com.qdapps.arduino;

import java.io.File;

import org.apache.commons.io.FileUtils;

import cc.arduino.Arduino;

public class TestUdoo {

	/**
	 * @param args
	 */
	public static String out = "out";
	public static String in = "in";
	
	public static void main(String[] args) throws Exception{
		if (args == null || args.length ==0){
			args = new String [] {
					"/sys/class/gpio/gpio40/direction",
					"/sys/class/gpio/gpio40/value"
			};
		}
		int i = 0;
		File modeFile = new File(args[0]);
		log ("Mode -> out");
		FileUtils.writeStringToFile(modeFile, out);
		
		File file = new File(args[1]);
		while (i < 15){
			try{
				Thread.sleep(1000);
				i++;
				FileUtils.writeStringToFile(file, i%2 == 0 ? "0": "1");
				log(".");
				//arduino.
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		log ("Mode -> in");
		FileUtils.writeStringToFile(modeFile, in);
		log ("End.");
	}

	private static void log(String s) {
		System.out.println(s);
		
	}

}
