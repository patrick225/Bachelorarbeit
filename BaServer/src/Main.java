import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException {

		 restart();
	}

	public static void restart() {

		try {
			new Game();
		} catch (Exception e) {
			System.out.println("Fatal Error");
			e.printStackTrace();
			System.out.println("Restarting...");
			System.exit(1);
			restart();
		}
	}

}
