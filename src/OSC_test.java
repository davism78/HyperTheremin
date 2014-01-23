import com.illposed.osc.*;
import java.net.*;

public class OSC_test {
	public static void main(String[] args) throws UnknownHostException, SocketException, java.io.IOException, InterruptedException {
	    // The IP Address of the server (or listener) we would like to send to
	    // For testing we will use the `getLocalHost` function to send to our
	    // machine, but for using with different physical devices, use something
	    // like the following commented out line
	    //InetAddress remoteIP  = InetAddress.getByName("192.168.1.1");
	    InetAddress remoteIP = InetAddress.getLocalHost();
	    
	    // The port that the server we are sending to is listening on
	    int remotePort = 8000;
	    
	    // Bring the IP Address and port together to form our OSC Sender
	    OSCPortOut sender = new OSCPortOut(remoteIP, remotePort);
	    
	    // The address to which we send our message
	    String address1 = "/note";
	    
	    // An array of objects that are our values we would like to send
	    Object values1[] = new Object[2];
	    values1[0] = new Integer(440);
	    values1[1] = new Integer(75);
	    
	    // Bring the address and values together to form an OSCMessage
	    OSCMessage message1 = new OSCMessage(address1, values1);
	   
	    // Send each message
	    System.out.printf("Sending message1 to %s:%s at %s\n", remoteIP, remotePort, message1.getAddress());
	    sender.send(message1);
	    
	  }
	}
	