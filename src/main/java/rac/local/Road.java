package rac.local;

public class Road {
	private int []x ;
	private int []y ;
	private int []z ;
	
	private final static String [] Track1 =
	{   "d1","e1","f2","g3","h4","i4","j4","k3","l2","m2","n3","n4","n5","n6",
		"n7","m8","l9","k9","j8","i8","h9","g9","f10","f11","f12","g13","h13",
		"i14","i15","i16","h17","g17","f17","e17","d16","c15","b14","a13",
		"a12","a11","a10","a9","a8","a7","a6","a5","a4","a3","b2","c1"
	} ;
	
	public Road() {
		this( Track1 ) ;
	}
	
	public Road( String [] roadCentreCoords ) {
		x = new int[ roadCentreCoords.length ] ;
		y = new int[ roadCentreCoords.length ] ;
		z = new int[ roadCentreCoords.length ] ;
		
		for( int i=0 ; i<roadCentreCoords.length ; i++ ) {
			char c=roadCentreCoords[i].charAt(0) ;
			x[i] = Character.toLowerCase(c) - 'a' ;
			y[i] = 0 ; //1 * (i&1) ;
			z[i] = 1 - Integer.parseInt(roadCentreCoords[i].substring(1) )  ;
		}
		
//		x = new int[] { -1, -1,  1,  1 ,  1,  1, -1, -1, -1, -1,  1,  1,  1,  1, -1, -1 } ;
//		y = new int[] { -1,  1,  1, -1 , -1,  1,  1, -1, -1,  1,  1, -1, -1,  1,  1, -1 } ;
//		z = new int[] { -1, -1, -1, -1 , -2, -2, -2, -2, -3, -3, -3, -3, -6, -6, -6, -6 } ;
	}
	
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

	public int[] getCoords( int index ) {
		int ix = index % x.length ;
		return new int[] { x[ix],y[ix],z[ix] } ;
	}
	public int clampClock( int index ) {
		if ( index < 0 ) return index + x.length ; 
		return index % x.length ;
	}
	
	public float[][] draw( float[] camera, float[] centre ) {

		float [] modelTranslation = new float[] { 0,0,0 } ;
		float [] modelRotation = new float [] { 0,0,0 } ;
		float [] modelScale = new float[] { 25,25,25 } ;
		
		float [][] rc = new float[3][x.length] ;
		for( int i=0 ; i<x.length ; i++ ) {
			int [] point = getCoords( i ) ;
			float [] p = new float[] { point[0], point[1], point[2] } ;
			transform( 
					p, 
					modelTranslation, modelRotation, modelScale, 
					camera, centre,
					1.25f, 1.25f, 0.1f, 1000.f 
					);
//			System.out.println( point[0] + "," + point[1] + "," + point[2] ) ;
			rc[0][i] = p[0] ;
			rc[1][i] = p[1] ;
			rc[2][i] = p[2] ; 
		}
		
		return rc ;
	}
	
	protected float[] lookAt( float [] camera, float [] centre, float [] up ) {
		float [] F = new float [] { centre[0] - camera[0], centre[1] - camera[1], centre[2] - camera[2] } ; 
		float [] U = new float [] { up[0], up[1], up[2] } ; 
		
		normalize( F ) ;
		
		float [] s = cross3( U, F ) ;
		normalize( s ) ;
		
		float [] u = cross3( F, s ) ;   

		
		float [] M = new float[] {  
				s[0], u[0], F[0], 0,
				s[1], u[1], F[1], 0,
				s[2], u[2], F[2], 0,
				0, 0, 0, 1 } ;
		
		float [] T = new float[] {
				1,0,0,0,
				0,1,0,0,
				0,0,1,0,
				-camera[0], -camera[1], -camera[2], 1 } ;
		
		return multiply4x4Matrix(M, T) ;
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
	protected void transform( 
			float [] point, 
			float [] modelTranslation, float [] modelRotation, float [] modelScale,
			float [] camera, float [] target,
			float width, float height, float near, float far
			) {
		float r = width / 2.f ;
		float t = height / 2.f ;
		float [] projectionMatrix = new float[] {
				near/r,0,0,0,
				0,near/t,0,0,
				0,0,-(far+near)/(far-near), -1,
				0,0,(-2.f*far*near)/(far-near), 0
		} ;
		
		float [] viewMatrix = lookAt( camera, target, new float[] { 0,1,0 } ) ;
		float [] modelMatrix = transform( modelTranslation, modelRotation, modelScale ) ;
		transformPoint2(
				multiply4x4Matrix( multiply4x4Matrix( projectionMatrix, viewMatrix), modelMatrix ), point ) ;
	}
	
	protected float[] transform( float [] translation, float [] rotation, float [] scale ) {
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
		
		return multiply4x4Matrix( 					
					multiply4x4Matrix(translationMatrix,rotationMatrix),
					scaleMatrix
					) ; 
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

	protected void transformPoint2( float [] transformation, float [] point) {
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
}
