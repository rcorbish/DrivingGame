package rac.local;

public class DrivingGame {

	public static void main( String args[] ) {
		try {
			Car car = new Car() ;
			Road road = new Road() ;
			
		} catch( Throwable t ) {
			t.printStackTrace();
			System.exit( -1 );
		}
	}
}
