public class Driver {

	public static void main(String[] args) {

		Main main = new Main();
		int retCode = main.run();
		
		System.out.println("Exited with return code " + retCode + ".");
	}

}