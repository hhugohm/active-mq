package org.neos.jms.mq.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.neos.jms.mq.ChatConexion;



public class TestChat {
	
	public static void main(String[] args) {
		try {
			if (args.length != 3)
				System.out.println("Factory, Topic, or username missing");
			// args[0]=topicFactory; args[1]=topicName; args[2]=username
			ChatConexion chat = new ChatConexion(args[0], args[1], args[2]);
			// Read from command line
			BufferedReader commandLine = new java.io.BufferedReader(new InputStreamReader(System.in));
			// Loop until the word "exit" is typed
			while (true) {
				String s = commandLine.readLine();
				if (s.equalsIgnoreCase("exit")) {
					chat.close();
					System.exit(0);
				} else
					chat.writeMessage(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//TopicCF topic1 neosssoftware

}
