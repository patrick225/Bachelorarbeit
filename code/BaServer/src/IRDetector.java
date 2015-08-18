import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;


public class IRDetector implements Runnable {

	private Pin PIN_IR = RaspiPin.GPIO_16;
	private volatile boolean blink;
	
	private int PULSWIDTH_HIGH = 24;
	private int PULSWIDTH_LOW  = 30;
	private int pulswidth;
	
	private GpioPinDigitalOutput led;
	public IRDetector() {

		GpioController gpio = GpioFactory.getInstance();
		led = gpio.provisionDigitalOutputPin(
				PIN_IR, 
				"ir_led",
				PinState.LOW);	
		
	}
	
	public void run() {
		blink = true;
		
		while (blink) {
			led.low();
			Gpio.delayMicroseconds(1);
			led.high();
			Gpio.delayMicroseconds(1);
		}
	}
	
	public void blinkHighFrequency() {
		pulswidth = PULSWIDTH_HIGH;
		new Thread(this).start();
	}
	
	public void blinkLowFrequency() {
		pulswidth = PULSWIDTH_LOW;
		new Thread(this).start();
	}
	
	public void stopBlink() {
		blink = false;
	}

}
