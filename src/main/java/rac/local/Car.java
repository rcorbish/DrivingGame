package rac.local;

public class Car {

	private int speed ;
	private int direction ;
	
	public Car() {
		this.speed = 0 ;
		this.direction = 0 ;
	}
	
	public void accelerate() {
		speed++ ;
	}
	public void brake() {
		speed-- ;
	}
	public void steerLeft() {
		direction-- ;
	}
	public void steerRight() {
		direction++ ;
	}

	public int getSpeed() {
		return speed;
	}

	public int getDirection() {
		return direction;
	}
}
