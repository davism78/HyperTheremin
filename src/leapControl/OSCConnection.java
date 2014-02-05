package leapControl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

public class OSCConnection {

	private InetAddress remoteIP;
	private int remotePort;
	private String nodeName;
	private OSCPortOut sender;

	public OSCConnection(int port, String name) {
		remotePort = port;
		nodeName = name;
		// The IP Address of the server (or listener) we would like to send to
	    // For testing we will use the `getLocalHost` function to send to our
	    // machine, but for using with different physical devices, use something
	    // like the following commented out line
	    //InetAddress remoteIP  = InetAddress.getByName("192.168.1.1");
    	try {
			remoteIP = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    // The port that the server we are sending to is listening on
	    
		try {
			sender = new OSCPortOut(remoteIP, remotePort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        System.out.println("Initialized");
	}

	
	public boolean sendPitch(double tone, double level){
		// Check for maximum allowed values
		if (level > 100.0){
       		level = 100.0;
		}
		if (tone > 20000.0) {
			tone = 20000.0;
		}
		return sendOSCMessage(nodeName, new Float(tone), new Float(level));
	}

	private boolean sendOSCMessage(String address, Object value1, Object value2){
		if(value1 == null || value2 == null)
			return false;
		Collection<Object> values = new ArrayList<Object>();
		values.add(value1);
		values.add(value2);
		OSCMessage message = new OSCMessage(address, values);
		try {
			sender.send(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}
}
