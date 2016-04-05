package rac.local;

import java.util.ArrayList;
import java.util.List;

public class Road {
	private float []x ;
	private float []y ;
	private float []z ;
	
	private final static String [] Track1 =
	{   "d1","e1","f2","g3","h4","i4","j4","k3","l2","m2","n3","n4","n5","o6","o7",
		"n7","m8","l9","k9","j8","i8","h9","g9","f10","f11","f12","g13","h13",
		"i14","i15","i16","h17","g17","f17","e17","d16","c15","b14","a13",
		"a12","a11","a10","a9","a8","a7","a6","a5","a4","a3","b2","c1"
	} ;
	private final static String [] Track2 =
	{   "d2","e2","f3","g4","h5","i5","j5","k4","l3","m3","m4","m5","m6","m7",
		"l8","k8","j7","i7","h8","g8","f9","e10","e11","e12","f13","g14","h14",
		"h15","h16","g16","f16","e16","d15","c14","b13","b12","b11","b10","b9",
		"b8","b7","b6","b5","b4","b3",
	} ;
	
	public Road() {
		this( Track1, Track2 ) ;
	}
	
	public Road( String [] roadCentreCoords, String [] roadCentreCoords2 ) {
		int SCALE = 6 ;
	    List<Point> path = new ArrayList<Point>();

		for( int i=0 ; i<roadCentreCoords.length ; i+=3 ) {
			char c=roadCentreCoords[i].charAt(0) ;
			int x0 = SCALE * (Character.toLowerCase(c) - 'a' ) ;			
			int y0 = SCALE * (Integer.parseInt(roadCentreCoords[i].substring(1) ) - 1 ) ;

			c=roadCentreCoords[i+1].charAt(0) ;
			int x1 = SCALE * (Character.toLowerCase(c) - 'a' ) ;			
			int y1 = SCALE * (Integer.parseInt(roadCentreCoords[i+1].substring(1) ) - 1) ;

			c=roadCentreCoords[i+2].charAt(0) ;
			int x2 = SCALE * (Character.toLowerCase(c) - 'a' );			
			int y2 = SCALE * (Integer.parseInt(roadCentreCoords[i+2].substring(1) ) - 1) ;

			int ix = i+3 ; 
			if( ix >= roadCentreCoords.length ) ix = 0 ; 
			c=roadCentreCoords[ix].charAt(0) ;
			int x3 = SCALE * (Character.toLowerCase(c) - 'a' ) ;			
			int y3 = SCALE * (Integer.parseInt(roadCentreCoords[ix].substring(1) ) - 1 ) ;

			generateBezierPath(path, new Point(x0,y0), new Point(x3,y3), new Point(x1,y1), new Point(x2,y2), 15) ;
		}

		
		for( int i=0 ; i<roadCentreCoords2.length ; i+=3 ) {
			char c=roadCentreCoords2[i].charAt(0) ;
			int x0 = SCALE * (Character.toLowerCase(c) - 'a' ) ;			
			int y0 = SCALE * (Integer.parseInt(roadCentreCoords2[i].substring(1) ) - 1 ) ;

			c=roadCentreCoords2[i+1].charAt(0) ;
			int x1 = SCALE * (Character.toLowerCase(c) - 'a' ) ;			
			int y1 = SCALE * (Integer.parseInt(roadCentreCoords2[i+1].substring(1) ) - 1) ;

			c=roadCentreCoords2[i+2].charAt(0) ;
			int x2 = SCALE * (Character.toLowerCase(c) - 'a' );			
			int y2 = SCALE * (Integer.parseInt(roadCentreCoords2[i+2].substring(1) ) - 1) ;

			int ix = i+3 ; 
			if( ix >= roadCentreCoords2.length ) ix = 0 ; 
			c=roadCentreCoords2[ix].charAt(0) ;
			int x3 = SCALE * (Character.toLowerCase(c) - 'a' ) ;			
			int y3 = SCALE * (Integer.parseInt(roadCentreCoords2[ix].substring(1) ) - 1 ) ;

			generateBezierPath(path, new Point(x0,y0), new Point(x3,y3), new Point(x1,y1), new Point(x2,y2), 15) ;
		}

		x = new float[ path.size() ] ;
		y = new float[ path.size() ] ;
		z = new float[ path.size() ] ;
		
		for( int i=0 ; i<path.size() ; i++ ) {
			Point p = path.get(i) ;
			x[i] = p.x ;
			y[i] = 0 ;
			z[i] = p.y ;
		}
//		x = new float[] { -30.f,  30.f, -30.f,  30.f, -30.f,  30.f, -30.f,  30.f } ;
//		y = new float[] { -30.f, -30.f, -30.f, -30.f,  30.f,  30.f,  30.f,  30.f } ;
//		z = new float[] { -30.f, -30.f,  30.f,  30.f, -30.f, -30.f,  30.f,  30.f } ;
	}
	
	
	public void generateBezierPath( List<Point> path, Point origin, Point destination, Point control1, Point control2, int segments) {
		float t = 0;
	    for (int i = 0; i < segments; i++) {
	        Point p = new Point( 
		        (float)(Math.pow(1 - t, 3) * origin.x + 3.0f * Math.pow(1 - t, 2) * t * control1.x + 3.0f * (1 - t) * t * t * control2.x + t * t * t * destination.x),
		        (float)(Math.pow(1 - t, 3) * origin.y + 3.0f * Math.pow(1 - t, 2) * t * control1.y + 3.0f * (1 - t) * t * t * control2.y + t * t * t * destination.y)
	        ) ;
	        t += 1.0f / segments;
	        path.add(p);
	    }
	    path.add(destination);
	} 
	
	class Point { Point(float x,float y) { this.x=x ; this.y=y ; } float x,y ; } 
	
	public int identifyTrackPosition( int xc, int zc ) {
		double minDistance =  Double.MAX_VALUE ;
		int position = -1 ;
		for( int i=0 ; i<x.length ; i++ ) {
			double distance = Math.sqrt( (xc-x[i]) * (xc-x[i]) + (zc-z[i]) * (zc-z[i]) ) ;
			if( distance < minDistance ) {
				if( distance<50 ) {
					position = i ;
				}
				minDistance = distance ;
			}
		}
		return position ;
	}

	public float[] getCoords( int index ) {
		int ix = index % x.length ;
		return new float[] { x[ix],y[ix],z[ix] } ;
	}
	
	public int clampClock( int index ) {
		if ( index < 0 ) return index + x.length ; 
		return index % x.length ;
	}
	
	public float[][] draw( float fov, float[] camera, float[] centre, float near, float far ) {

		float [][] rc = new float[3][x.length] ;

		float [] modelTranslation = new float[] { 0,0,0 } ;
		float [] modelRotation = new float [] { 0,0,0 } ;
		float [] modelScale = new float[] { 1,1,1 } ;
		
		for( int i=0 ; i<x.length ; i++ ) {
			float [] p = getCoords( i ) ;
			transform( 
					p, 
					modelTranslation, modelRotation, modelScale, 
					camera, centre,
					fov, 1.f, 
					near, far 
					) ;
//			if( p[2] > near && p[2]<far ) {
				rc[0][i] = p[0] ;
				rc[1][i] = p[1] ;
				rc[2][i] = p[2] ;
//			} else {
//				rc[0][i] = Float.NaN ;
//				rc[1][i] = Float.NaN ;
//				rc[2][i] = Float.NaN ;
//			}
		}
		
		return rc ;
	}
	
	protected float[] lookAt( float [] camera, float [] centre, float [] up ) {
		
		float [] F = new float [] { centre[0] - camera[0], centre[1] - camera[1], centre[2] - camera[2] } ; 
		normalize( F ) ;
		
		float [] U = new float [] { up[0], up[1], up[2] } ; 
		normalize( U ) ;
		
		float [] s = cross3( F, U ) ;
		normalize( s ) ;
		
		float [] u = cross3( s, F ) ;   
		
		float [] M = new float[] {  
				s[0], u[0], -F[0], 0,
				s[1], u[1], -F[1], 0,
				s[2], u[2], -F[2], 0,
				0, 0, 0, 1 } ;
		
		float [] T = new float[] {
				1,0,0,0,
				0,1,0,0,
				0,0,1,0,
				-camera[0], -camera[1], -camera[2], 1 } ;
		
		return multiply4x4Matrix(M, T) ;
	}
	

	protected void transform( 
			float [] point, 
			float [] modelTranslation, float [] modelRotation, float [] modelScale,
			float [] camera, float [] target,
			float fov, float aspect,
			float near, float far
			) {
		
		float t = 1.f / (float) Math.tan(Math.toRadians(fov / 2.0) );
		
		float [] projectionMatrix = new float[] {
				(t/aspect),0,0,0,
				0,t,0,0,
				0,0,-far/(far-near), -1,
				0,0,-(far*near)/(far-near), 0
		} ;
		
		float [] viewMatrix = lookAt( camera, target, new float[] { 0,1,0 } ) ;
		float [] modelMatrix = buildTransformation( modelTranslation, modelRotation, modelScale ) ;
		
		transformPoint( multiply4x4Matrix( projectionMatrix, viewMatrix, modelMatrix ), point ) ;
		point[0] /= point[2] ;
		point[1] /= point[2] ;
		//point[2] /= point[2] ;
	}
	
	protected float[] buildTransformation( float [] translation, float [] rotation, float [] scale ) {
		float [] translationMatrix = new float[] { 
				1,0,0,0,  
				0,1,0,0,  
				0,0,1,0, 
				translation[0], translation[1], translation[2], 1 
				} ;
		
		float ca = (float) Math.cos( Math.toRadians(rotation[0]) ) ;
		float sa = (float) Math.sin( Math.toRadians(rotation[0]) ) ;
		float cb = (float) Math.cos( Math.toRadians(rotation[1]) ) ;
		float sb = (float) Math.sin( Math.toRadians(rotation[1]) ) ;
		float cg = (float) Math.cos( Math.toRadians(rotation[2]) ) ;
		float sg = (float) Math.sin( Math.toRadians(rotation[2]) ) ;
		
		float [] rotationMatrix = new float[] { 
				cb*cg, ca*sg + sa*sb*sg, sa*sg - ca*sb*cg, 0,
				-cb*sg, ca*cg -sa*sb*sg, sa*sg + ca*sb*cg, 0,
				sb, -sa*cb, ca*cb, 0,
				0,0,0,1
		};
		
		float [] scaleMatrix = new float[] {
				scale[0],0,0,0,  0,scale[1],0,0,  0,0,scale[2],0,   0,0,0,1 
		};
		
		return multiply4x4Matrix( translationMatrix, rotationMatrix, scaleMatrix ) ; 
	}
	
	protected void transformPoint1( float [] point, float [] transformation ) {
		float x = transformation[0] * point[0] + 
				transformation[1] * point[1]  +
				transformation[2] * point[2]  +
				transformation[3] ;
		float y = transformation[4] * point[0] + 
				transformation[5] * point[1]  +
				transformation[6] * point[2]  +
				transformation[7] ;
		float z = transformation[8] * point[0] + 
				transformation[9] * point[1]  +
				transformation[10] * point[2]  +
				transformation[11] ;
//		float w = transformation[12] * point[0] + 
//				transformation[13] * point[1]  +
//				transformation[14] * point[2]  +
//				transformation[15] ;
		point[0] = x ;
		point[1] = y ;
		point[2] = z ;
	}

	protected void transformPoint( float [] transformation, float [] point) {
		float x = transformation[0] * point[0] + 
				transformation[4] * point[1]  +
				transformation[8] * point[2]  +
				transformation[12] ;
		float y = transformation[1] * point[0] + 
				transformation[5] * point[1]  +
				transformation[9] * point[2]  +
				transformation[13] ;
		float z = transformation[2] * point[0] + 
				transformation[6] * point[1]  +
				transformation[10] * point[2]  +
				transformation[14] ;
//		float w = transformation[3] * point[0] + 
//				transformation[7] * point[1]  +
//				transformation[11] * point[2]  +
//				transformation[15] ;
		point[0] = x ;
		point[1] = y ;
		point[2] = z ;
	}
	

	public float [] multiply4x4Matrix( float [] a, float [] b, float [] c ) {
		float[] m = multiply4x4Matrix( a, b ) ;
		return multiply4x4Matrix( m,c ) ;
	}

	
	public float [] multiply4x4Matrix( float [] left, float [] right ) {
		
		float [] rc = new float[16];

		for (int i = 0; i < 4; i++) {
	        for (int j = 0; j < 4; j++) {
	            rc[j * 4 + i] = 0;

	            for (int k = 0; k < 4; k++) {
	                rc[j * 4 + i] += left[k * 4 + i] * right[j * 4 + k];
	            }
	        }
	    }
		
		return rc ;
	}

	protected float[] cross3( float[] a, float []b ) {
		float [] rc = new float[3] ;
		rc[0] = a[1]*b[2] - a[2]*b[1] ;
		rc[1] = a[2]*b[0] - a[0]*b[2] ;
		rc[2] = a[0]*b[1] - a[1]*b[0] ;		
		return rc ;
	}
	
	protected void normalize( float[] vector ) {
		float total = 0 ;
		for( int i=0 ; i<vector.length ; i++ ) {
			total += vector[i] * vector[i] ;
		}
		total = (float)Math.sqrt( total ) ;
		for( int i=0 ; i<vector.length ; i++ ) {
			vector[i] /= total ;
		}
	}
}

