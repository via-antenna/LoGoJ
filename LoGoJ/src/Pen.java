
public class Pen {	// This class will be used to draw the lines the program requires. Will draw from offsetX and offsetY to
					// new offset X and new offset Y, using color as described below. Orientation is used with some trig
					// to calculate the new offsets first. Will also be used to draw turtle (centered at offset)

	private double offsetX;
	private double offsetY;
	private double newOffsetX;
	private double newOffsetY;
	private double orientation;	// Note that the orientation of 0 is down and 180 is up
	
	public Pen() {
		offsetX = 400;
		offsetY = 400;
		orientation = 180;
	}
	
	public double getOffsetX() {
		return offsetX;
	}
	
	public double getOffsetY() {
		return offsetY;
	}
	
	public double getNewOffsetX() {
		return newOffsetX;
	}
	
	public double getNewOffsetY() {
		return newOffsetY;
	}
	
	public double getOrientation() {
		return orientation;
	}
	
	public void setOrientation(double d) {
		orientation = d;
	}
	
	public void setOffsetX(double d) {
		offsetX = d;
	}
	
	public void setOffsetY(double d) {
		offsetY = d;
	}

	public void setNewOffsetX(double d) {
		newOffsetX = d;
	}

	public void setNewOffsetY(double d) {
		newOffsetY = d;
	}
	
}