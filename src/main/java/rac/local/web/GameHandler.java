package rac.local.web;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import rac.local.Road;

/**
 * This will pull a jpeg from directory and return its data as a raw stream.
 * This only supports jpeg files at present.
 * 
 * @author richard
 *
 */
public class GameHandler extends AbstractHandler {

	final private Road road ;

	public GameHandler( Road road ) {
		this.road = road ;
	}

	@Override
	public void handle(String arg0, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// If file is OK - send it back as a stream.

		try {
			PrintWriter pw = response.getWriter() ;
			if( arg0.equals("/") ) {
				response.setStatus( HttpServletResponse.SC_OK );	
				response.setContentType("text/html");
				printBrainPage(pw);
			} else if (arg0.equals("/data") ) {
				response.setStatus( HttpServletResponse.SC_OK );	
				response.setContentType("application/json");
				printRoadData(pw ) ;
			} else {
				response.setStatus( HttpServletResponse.SC_NOT_FOUND );	
			}

		} finally {
			response.flushBuffer();
		}
		baseRequest.setHandled( true ) ;		
	}

	protected void printRoadData( PrintWriter pw ) {
/*
		int[][] coords = road.draw(0, 0, 0, -10 ) ;

		pw.append( "{ \"coords\":[" ) ;
		char sep = ' ' ;
		for( int i=0 ; i<coords[0].length ; i++ ) {
			if( coords[2][i] != 0 ) {
				pw.append(sep).append( "{\"x\":" ).append( String.valueOf(coords[0][i]) ).
				append( ", \"y\":" ).append( String.valueOf(coords[1][i]) ).append( "}" ) ;
				sep = ',' ;
			}
		}
		pw.append( "] }" ) ;
		*/
	}


	protected void printBrainPage( PrintWriter pw ) {
		File brainHtmlFile = new File( "src/main/resources/road.html" ) ;
		if( !brainHtmlFile.exists() ) {
			brainHtmlFile = new File( "./road.html" ) ;
		}
		try ( FileReader fr = new FileReader( brainHtmlFile ) ) {
			char[] buf = new char[1024] ;

			int n ; 
			do { 
				n = fr.read(buf);
				if( n > 0 ) {
					pw.write(buf, 0, n );
				}
			} while( n > 0 ) ;
		} catch( IOException ioe ) {
			pw.write( "Error:" + ioe.getMessage() ) ;
		}
	}
}


