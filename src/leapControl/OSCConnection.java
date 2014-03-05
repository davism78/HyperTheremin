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

	private static final String FILENAME = "recording.wav";
	
	private InetAddress remoteIP;
	private int remotePort;
	private OSCPortOut sender;

	public OSCConnection(int port) {
		remotePort = port;
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
		Collection<Object> values = new ArrayList<Object>();
		values.add(new Float(tone));
		values.add(new Float(level));
		return sendOSCMessage("/note", values);
	}
	
	public boolean sendRecordOn(){
		Collection<Object> values = new ArrayList<Object>();
		values.add(FILENAME);
		return sendOSCMessage("/recon", values);
	}
	
	public boolean sendRecordOff(){
		Collection<Object> values = new ArrayList<Object>();
		values.add(FILENAME);
		return sendOSCMessage("/recoff", values);
	}

	private boolean sendOSCMessage(String address, Collection<Object> values){
		if(values == null){
			return false;
		}
		
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
