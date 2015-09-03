
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
			restart();
		}
	}

}
