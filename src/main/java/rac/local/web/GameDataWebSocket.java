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
	private int direction = 90 ;
	private float x = 0 ;
	private float y = 0 ;
	private float z = 2 ;
	private int clock = 0 ;
	
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
		//executor.scheduleAtFixedRate(() ->  sendBrainState(), 0, 75, TimeUnit.MILLISECONDS);
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
		//int val = Integer.parseInt( message.substring(1)) ;
		if( message.startsWith( "L" ) ) direction -- ;
		if( message.startsWith( "R" ) ) direction ++ ;
		if( direction<0 ) direction += 360 ;
		if( direction>360 ) direction -= 360 ;

		x = z * (float)Math.cos( Math.toRadians(direction) ) ;
		y = z * (float)Math.sin( Math.toRadians(direction) ) ;
		//y = 2 ;

		if( message.startsWith( "B" ) ) clock-- ; //{ x += dx ; y += dy ; }
		if( message.startsWith( "F" ) ) clock++ ; //{ x -= dx ; y -= dy ; }
		clock = road.clampClock( clock ) ;

		System.out.println( "clk: " + clock  ) ;
		sendRoadImage() ;
	}
	// sends message to browser
	public void sendRoadImage() {
		try {
			if (session.isOpen()) {
				StringBuilder sb = new StringBuilder() ;
				float[] p1 		= road.getCoords(clock) ;
				float[] p2 		= road.getCoords(clock+1) ;
				float[] eye    	= new float[] { p1[0], p1[1]+.002f, p1[2] } ;
				float[] target 	= new float[] { p2[0], p2[1], p2[2] } ;
				float[][] coords = road.draw( eye, target ) ;

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


