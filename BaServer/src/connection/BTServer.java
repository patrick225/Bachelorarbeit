package connection;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
public class BTServer {

	
	
	public BTServer() {

		LocalDevice ld = null;
		
		StreamConnectionNotifier notifier;
		StreamConnection conn = null;
		try {
			ld = LocalDevice.getLocalDevice();
			
			ld.setDiscoverable(DiscoveryAgent.GIAC);
		} catch (BluetoothStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
