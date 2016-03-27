package rac.local.web;

import rac.local.Road;

public class Main {

	public static void main(String[] args) {
		try {
		Road road = new Road() ;
		WebServer server = new WebServer(road) ;
		} catch( Throwable t ) {
			t.printStackTrace(); 
			System.exit( -1 ); 
		}
	}

}
