package rac.local;


import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMatrix {

	Road underTest ;
	
	@Before
	public void setup() {
		underTest = new Road() ;
	}
	@After 
	public void tearDown() {
		underTest = null ;
	}
	
	@Test
	public void test1() {
		float[] left = new float[] { 1,2,3,4,2,3,4,5,3,4,5,6,4,5,6,7 } ;
		float [] right = new float[] { 1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1 } ;
		
		float[] ans = underTest.multiply4x4Matrix(left, right) ;
		
		Assert.assertArrayEquals(left, ans, 0.0000001f);
	}
	
	@Test
	public void test2() {
		float[] left = new float[] { 1,5,9,13,2,6,10,14,3,7,11,15,4,8,12,16 } ;
		float [] right = new float[] { 10,14,18,22,11,15,19,23,12,16,20,24,13,17,21,2 } ;
		float [] expected = new float[] { 180,436,692,948,190,462,734,1006,200,488,776,1064,118,330,542,754 } ;
		float[] ans = underTest.multiply4x4Matrix(left, right) ;
		
		Assert.assertArrayEquals(expected, ans, 0.0000001f);
	}

	@Test
	public void test3() {
		int [] point = new int[] { 1,2,3 } ;
		
		int [] modelTranslation = new int[] { 0,0,0 } ;
		int [] modelRotation = new int [] { 0,0,0 } ;
		int [] modelScale = new int[] { 10,10,10 } ;

		int [] viewTranslation = new int[] { 0,0,0 } ;
		int [] viewRotation = new int [] { 90,0,0 } ;
		int [] viewScale = new int[] { 10,10,10 } ;

//		underTest.transform( point, 
//				modelTranslation, modelRotation, modelScale, 
//				viewTranslation, viewRotation, viewScale,
//				600,400,0.1f,30.f 
//				);
		
		System.out.println( point[0] + "," + point[1] + "," + point[2] ) ;
	}

	@Test
	public void test4() {
		float coords[][] = underTest.draw( new float[]{5, 0, 10 }, new float[]{0,0,0} );
		
		int SCREEN_SIZE=200 ;
		boolean screen[][] = new boolean[SCREEN_SIZE][SCREEN_SIZE] ;
		screen = new boolean[SCREEN_SIZE][] ;
		for( int i=0 ; i<SCREEN_SIZE ; i++ ) {
			screen[i] = new boolean[SCREEN_SIZE] ;
		}
		for( int i=0 ; i<coords[0].length ; i++ ) {
			System.out.println( coords[0][i] + "," + coords[1][i] ) ;
			int x = (int)(SCREEN_SIZE/2+coords[0][i] ) ;
			int y = (int)(SCREEN_SIZE/2+coords[1][i] ) ;
			screen[x][y] = true ;
		}
		for( int y=0 ; y<SCREEN_SIZE ; y++ ) {
			for( int x=0 ; x<SCREEN_SIZE ; x++ ) {
				System.out.print( screen[x][y] ? "*" : "-" ) ;
			}
			System.out.println() ;
		}
	}
}
