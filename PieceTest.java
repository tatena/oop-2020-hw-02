import junit.framework.TestCase;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

/*
  Unit test for Piece class -- starter shell.
 */
public class PieceTest extends TestCase {
	private Piece stickR1, stickR2;
	private Piece L1R1, L1R2, L1R3, L1R4;
	private Piece L2R1, L2R2, L2R3, L2R4;
	private Piece S1R1, S1R2;
	private Piece S2R1, S2R2;
	private Piece sqr;
	private Piece pyrR1, pyrR2, pyrR3, pyrR4;
	private Piece weird1, weird2;

	protected void setUp() throws Exception {
		super.setUp();
		stickR1 = new Piece(Piece.STICK_STR);
		L1R1 = new Piece(Piece.L1_STR);
		L2R1 = new Piece(Piece.L2_STR);
		S1R1 = new Piece(Piece.S1_STR);
		S2R1 = new Piece(Piece.S2_STR);
		sqr = new Piece(Piece.SQUARE_STR);
		pyrR1 = new Piece(Piece.PYRAMID_STR);
		weird1 = new Piece("2 0   3 0   4 0   5 0   1 1   2 2   0 2   0 3");
		weird2  = new Piece("1 0   0 1   1 1   2 1   3 1   4 2   5 1   6 2   6 3   6 4   6 5");
		getSlowRotations();
	}

	private void getSlowRotations() {
		stickR2  = stickR1.computeNextRotation();

		L1R2 = L1R1.computeNextRotation();
		L1R3 = L1R2.computeNextRotation();
		L1R4 = L1R3.computeNextRotation();

		L2R2 = L2R1.computeNextRotation();
		L2R3 = L2R2.computeNextRotation();
		L2R4 = L2R3.computeNextRotation();

		S1R2 = S1R1.computeNextRotation();

		S2R2 = S2R1.computeNextRotation();

		pyrR2 = pyrR1.computeNextRotation();
		pyrR3 = pyrR2.computeNextRotation();
		pyrR4 = pyrR3.computeNextRotation();
	}

	public void testSizeBasic() {
		assertEquals(1, stickR1.getWidth());
		assertEquals(4, stickR1.getHeight());

		assertEquals(2, L1R1.getWidth());
		assertEquals(3, L1R1.getHeight());

		assertEquals(2, L2R1.getWidth());
		assertEquals(3, L2R1.getHeight());

		assertEquals(3, S1R1.getWidth());
		assertEquals(2, S1R1.getHeight());

		assertEquals(3, S2R1.getWidth());
		assertEquals(2, S2R1.getHeight());

		assertEquals(2, sqr.getWidth());
		assertEquals(2, sqr.getHeight());

		assertEquals(3, pyrR1.getWidth());
		assertEquals(2, pyrR1.getHeight());

	}

	public void testSizeRotated() {
		assertEquals(4, stickR2.getWidth());
		assertEquals(1, stickR2.getHeight());

		assertEquals(2, L1R3.getWidth());
		assertEquals(3, L1R3.getHeight());

		assertEquals(3, L2R4.getWidth());
		assertEquals(2, L2R4.getHeight());

		assertEquals(2, S1R2.getWidth());
		assertEquals(3, S1R2.getHeight());

		assertEquals(2, S2R2.getWidth());
		assertEquals(3, S2R2.getHeight());

		assertEquals(2, sqr.computeNextRotation().getWidth());
		assertEquals(2, sqr.computeNextRotation().getHeight());

		assertEquals(2, pyrR2.getWidth());
		assertEquals(3, pyrR2.getHeight());
	}


	public void testSizeWeird() {
		assertEquals(6, weird1.getWidth());
		assertEquals(4, weird1.getHeight());

		assertEquals(7, weird2.getWidth());
		assertEquals(6, weird2.getHeight());

		// test weird rotated
		assertEquals(4, weird1.computeNextRotation().getWidth());
		assertEquals(6, weird1.computeNextRotation().getHeight());

		assertEquals(6, weird2.computeNextRotation().getWidth());
		assertEquals(7, weird2.computeNextRotation().getHeight());
	}

	public void testEqualsBasic() {
		Piece newStick = new Piece(Piece.STICK_STR);
		assertTrue(newStick.equals(newStick));
		assertTrue(newStick.equals(stickR1));
		assertTrue(newStick.equals(stickR2.computeNextRotation()));

		assertFalse(newStick.equals(pyrR1));
	}

	public void testEqualsWeird() {
		Piece longerStick = new Piece(Piece.STICK_STR + " 0 4   0 5");
		assertFalse(longerStick.equals(stickR1));
		assertFalse(stickR1.equals(longerStick));

		assertFalse(longerStick.equals(sqr));

		assertFalse(weird1.equals(weird2));
		assertTrue(weird1.equals(weird1));

		assertFalse(longerStick.equals(new TPoint(1, 2)));
	}

	public void testSkirtBasic() {
		assertTrue(Arrays.equals(new int[] {0}, stickR1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0}, L1R1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0}, L2R1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0, 1}, S1R1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 0}, S2R1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyrR1.getSkirt()));
	}

	public void testSkirtRotated() {
		assertTrue(Arrays.equals(new int[] {0, 0, 0, 0}, stickR2.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, L1R2.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 2}, L2R3.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, S1R2.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 1}, S2R2.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 1}, pyrR4.getSkirt()));
	}

	public void testSkirtWeird() {
		assertTrue(Arrays.equals(new int[]{2, 1, 0, 0, 0, 0}, weird1.getSkirt()));
		assertTrue(Arrays.equals(new int[]{1, 0, 1, 1, 2, 1, 2}, weird2.getSkirt()));
	}

	// rerun the previous tests on fast rotated pieces
	public void testFastRotations () {
		getFastRotations();
		testSizeRotated();
		testSkirtRotated();
		assertTrue(pyrR1.equals(pyrR4.fastRotation()));
		Piece.getPieces();
	}

	// change all slow rotated pieces with fast rotated ones
	private void getFastRotations() {
		Piece[] pieces = Piece.getPieces();

		stickR2  = pieces[Piece.STICK].fastRotation();

		L1R2 = pieces[Piece.L1].fastRotation();
		L1R3 = L1R2.fastRotation();
		L1R4 = L1R3.fastRotation();

		L2R2 = pieces[Piece.L2].fastRotation();
		L2R3 = L2R2.fastRotation();
		L2R4 = L2R3.fastRotation();

		S1R2 = pieces[Piece.S1].fastRotation();

		S2R2 = pieces[Piece.S2].fastRotation();

		pyrR2 = pieces[Piece.PYRAMID].fastRotation();
		pyrR3 = pyrR2.fastRotation();
		pyrR4 = pyrR3.fastRotation();

	}

	public  void testParsingException() {
		Exception exception  = assertThrows(RuntimeException.class, () -> new Piece("7 7 8-"));
		String expectedMessage = "Could not parse x,y string:";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}


}
