import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;


public class GoalDetector extends Thread {

	OnGoalDetected goalListener;
	
	private static final Pin pin = RaspiPin.GPIO_15;
	
	public GoalDetector(OnGoalDetected goalListener) {

		this.goalListener = goalListener;
		System.out.println("goal detection started...");
		
		start();
		
		
	}
	
	@Override
	public void run() {
		
		final GpioController gpio = GpioFactory.getInstance();
		final GpioPinDigitalInput goalValue = gpio.provisionDigitalInputPin(
				pin, 
				"taster",
				PinPullResistance.PULL_DOWN);		
		
		while(true) {
			if (goalValue.isHigh()) {
				goalListener.goalDetected(1);
				do {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} while(goalValue.isHigh());
			}
		}
		
	}

}
