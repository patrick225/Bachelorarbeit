package connection;

public interface StateListener {

	public void stateChanged(Channel who, int state);
}
