package connection;

public interface OnRobotMessageReceived {

	public void messageReceived(UDPConnectionHandler robot, byte[] data);
}
