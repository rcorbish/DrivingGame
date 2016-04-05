package rac.local.web;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import rac.local.Road;

/**
 *  
 * @author richard
 *
 */

public class GameDataWebSocket extends WebSocketAdapter {
	private int clock = 0 ;
	private int far = 140 ;
	private int near = 100 ;
	private int x = 100 ;
	private int y = 100 ;
	private int z = 100 ;
	private int fov = 100 ;
	
	private Session session;
	private final Road road ;
	private final ScheduledExecutorService executor ;

	public GameDataWebSocket( Road road ) {
		this.road = road ;
		executor = Executors.newScheduledThreadPool(1);
	}

	// called when the socket connection with the browser is established
	public void onWebSocketConnect(Session session) {
		System.out.println( "Connected WebSocket !!!!!" ) ;
		this.session = session;
		
		executor.scheduleAtFixedRate(() -> {
				clock = road.clampClock( clock ) ;
				float[] p1 		= road.getCoords(clock) ;
				float[] p2 		= road.getCoords(clock+1) ;
				float[] eye    	= new float[] { p1[0], y, p1[2] } ;
	
				sendRoadImage( fov, eye[0], eye[1], eye[2], p2[0], p2[1], p2[2], near, far ) ;
				clock++ ;
			}, 0, 50, TimeUnit.MILLISECONDS
		);
		
	}

	// called when the connection closed
	public void onWebSocketClose(int statusCode, String reason) {
		System.out.println("Connection closed with statusCode=" 
				+ statusCode + ", reason=" + reason);
	}

	// called in case of an error
	public void onWebSocketError(Throwable error) {
		error.printStackTrace();    
	}

	// called when a message received from the browser
	public void onWebSocketText(String message) {

		String s[] = message.split( "\\|" ) ;
		String msg = s[0] ;
		int val = Integer.parseInt( s[1] ) ;
		
		clock = road.clampClock( clock ) ;

		if( msg.equalsIgnoreCase( "x" ) ) x = val ;
		if( msg.equalsIgnoreCase( "y" ) ) y = val ;
		if( msg.equalsIgnoreCase( "z" ) ) z = val ;
		if( msg.equalsIgnoreCase( "near" ) ) near = val ;
		if( msg.equalsIgnoreCase( "far" ) ) far = val ;
		if( msg.equalsIgnoreCase( "fov" ) ) fov = val ;

		System.out.println( "fov, x, y, z, near, far " + fov + "," + x+ "," +y+ "," +z+ "," +near+ "," +far ) ;

		sendRoadImage( fov, x, y, z, near, far ) ;
	}
	
	// sends message to browser
	public void sendRoadImage( float fov, float x, float y, float z, float near, float far ) {
		sendRoadImage( fov, x, y, z, 0.f, 0.f, 0.f, near, far ) ;		
	}
	
	public void sendRoadImage( float fov, float x, float y, float z, float tx, float ty, float tz, float near, float far ) {
		try {
			if (session.isOpen()) {
				StringBuilder sb = new StringBuilder() ;
				float[] p1 		= road.getCoords(clock) ;
				float[] p2 		= road.getCoords(clock+1) ;
				float[] eye    	= new float[] { p1[0], p1[1]+3.0f, p1[2] } ;
				float[] target 	= new float[] { p2[0], p2[1], p2[2] } ;
				
				eye[0] = x ;
				eye[1] = y ;
				eye[2] = z ;
				
				target[0] = 0.f ;
				target[1] = 0.f ;
				target[2] = 0.f ;
				
				float[][] coords = road.draw( fov, eye, target, near, far ) ;

				sb.append( "{ \"coords\":[" ) ;
				char sep = ' ' ;
				for( int i=0 ; i<coords[0].length ; i++ ) {
					sb.append(sep).
						append( "{ \"x\":" ).append( String.valueOf(coords[0][i]) ).
						append( ", \"y\":" ).append( String.valueOf(coords[1][i]) ).
						append( ", \"z\":" ).append( String.valueOf(coords[2][i]) ).
						append( "}" ) ;
					sep = ',' ;
				}
				sb.append( "], \"clock\":" ).append( clock ).
				   append( ", \"clock2\":" ).append( road.clampClock(clock+1) ).append( " }" ) ;

				session.getRemote().sendString( sb.toString() ) ;
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	// closes the socket
	private void stop() {
		try {
			session.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}


