import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeAll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class BoardTest extends TestCase {
	private static Piece[] pieces;
	private Piece stickR1, stickR2;
	private Piece L1R1, L1R2, L1R3, L1R4;
	private Piece L2R1, L2R2, L2R3, L2R4;
	private Piece S1R1, S1R2;
	private Piece S2R1, S2R2;
	private Piece sqr;
	private Piece pyrR1, pyrR2, pyrR3, pyrR4;
	private Piece weird1, weird2;
	private Board b;

	protected void setUp() throws Exception{
		super.setUp();
		b = new Board(5, 6);
		b.setDebugMode(false);
		pieces = Piece.getPieces();
		getFastRotations();
	}

	private void getFastRotations() {
		stickR1 = pieces[Piece.STICK];
		L1R1 = pieces[Piece.L1];
		L2R1 = pieces[Piece.L2];
		S1R1 = pieces[Piece.S1];
		S2R1 = pieces[Piece.S2];
		sqr = pieces[Piece.SQUARE];
		pyrR1 = pieces[Piece.PYRAMID];

		stickR2  = stickR1 .fastRotation();

		L1R2 = L1R1.fastRotation();
		L1R3 = L1R2.fastRotation();
		L1R4 = L1R3.fastRotation();

		L2R2 = L2R1.fastRotation();
		L2R3 = L2R2.fastRotation();
		L2R4 = L2R3.fastRotation();

		S1R2 = S1R1.fastRotation();

		S2R2 = S2R1.fastRotation();

		pyrR2 = pyrR1.fastRotation();
		pyrR3 = pyrR2.fastRotation();
		pyrR4 = pyrR3.fastRotation();

	}

	public void testCtor() {
		Board b = new Board(5, 6);

		assertEquals(5 ,b.getWidth());
		assertEquals(6 ,b.getHeight());

		checkRowLengths(new int[]{0, 0, 0, 0, 0, 0}, b);
		checkColHeights(new int[]{0, 0, 0, 0, 0}, b);

		assertEquals(0, b.getMaxHeight());
		assertEquals(true, b.committed);
	}

	public void testPlaceNoCommit() {
		Board b = new Board(5, 6);

		int res = b.place(stickR1, 0, 0);
		assertEquals(Board.PLACE_OK, res);

		Exception exception  = assertThrows(RuntimeException.class, () -> b.place(stickR1, 1, 0));
		String expectedMessage = "place commit problem";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	public void testPlaceReturnOk() {
		int res = b.place(stickR2, 0, 0);
		assertEquals(Board.PLACE_OK, res);
		b.commit();

		res = b.place(pyrR2, 3, 1);
		assertEquals(Board.PLACE_OK, res);
		b.commit();

		res = b.place(S1R1, 1, 2);
		assertEquals(Board.PLACE_OK, res);
		b.commit();

		res = b.place(L2R4, 0, 4);
		assertEquals(Board.PLACE_OK, res);
		b.commit();

		checkColHeights(new int[]{6, 5, 5, 4, 4}, b);
		checkRowLengths(new int[]{4, 1, 4, 3, 3, 1}, b);
	}

	public void testPlaceReturnBad() {
		int res = b.place(stickR2, 0, 0);
		assertEquals(Board.PLACE_OK, res);
		b.commit();

		res = b.place(stickR1, 0, 0);
		assertEquals(Board.PLACE_BAD, res);
		b.undo();

		res = b.place(pyrR2, 1, 0);
		assertEquals(Board.PLACE_BAD, res);
		b.undo();

		res = b.place(pyrR2, 3, 1);
		assertEquals(Board.PLACE_OK, res);
		b.commit();

		res = b.place(S2R2, 2, 1);
		assertEquals(Board.PLACE_BAD, res);
		b.undo();

	}

	public void testPlaceReturnOutOfBounds() {
		int res = b.place(stickR2, 0, 6);
		assertEquals(Board.PLACE_OUT_BOUNDS, res);
		b.undo();

		res = b.place(stickR2, -1, 0);
		assertEquals(Board.PLACE_OUT_BOUNDS, res);
		b.undo();

		res = b.place(stickR2, 7, 0);
		assertEquals(Board.PLACE_OUT_BOUNDS, res);
		b.undo();

		res = b.place(stickR2, 0, -8);
		assertEquals(Board.PLACE_OUT_BOUNDS, res);
		b.undo();

		res = b.place(stickR1, 0, 4);
		assertEquals(Board.PLACE_OUT_BOUNDS, res);
		b.undo();
		b.undo();

		res = b.place(stickR1, 0, 4);
		assertEquals(Board.PLACE_OUT_BOUNDS, res);
		b.undo();

		res = b.place(L1R4, 4, 0);
		assertEquals(Board.PLACE_OUT_BOUNDS, res);
		b.undo();
	}

	public void testPlaceReturnRowFilled() {
		int res = b.place(stickR2, 0, 0);
		assertEquals(Board.PLACE_OK, res);
		b.commit();

		res = b.place(pyrR2, 3, 0);
		assertEquals(Board.PLACE_ROW_FILLED, res);
		b.commit();

		res = b.place(pyrR1, 0, 1);
		assertEquals(Board.PLACE_ROW_FILLED, res);
		b.commit();

		checkColHeights(new int[]{2, 3, 2, 2, 3}, b);
		checkRowLengths(new int[]{5, 5, 2, 0, 0, 0}, b);
	}

	public void testClearRowsBasic() {
		b.place(stickR2, 0, 0);
		b.commit();

		b.place(pyrR2, 3, 0);
		b.clearRows();
		b.commit();

		assertEquals(2, b.getMaxHeight());
		checkColHeights(new int[]{0, 0, 0, 1, 2}, b);
		checkRowLengths(new int[]{2, 1, 0, 0, 0, 0}, b);

		b.clearRows();

		assertEquals(2, b.getMaxHeight());
		checkColHeights(new int[]{0, 0, 0, 1, 2}, b);
		checkRowLengths(new int[]{2, 1, 0, 0, 0, 0}, b);
	}

	public void testClearRowsMultiple() {
		b.place(stickR2, 0, 0);
		b.commit();

		b.place(pyrR2, 3, 0);
		b.commit();

		b.place(pyrR1, 0, 1);
		b.clearRows();
		b.commit();

		assertEquals(1, b.getMaxHeight());
		checkColHeights(new int[]{0, 1, 0, 0, 1}, b);
		checkRowLengths(new int[]{2, 0, 0, 0, 0, 0}, b);
		b.toString();
	}

	public void testClearRowsGaps() {
		b.place(stickR1, 0, 0);
		b.commit();

		b.place(L1R1, 2, 0);
		b.commit();

		b.place(S1R2, 3, 0);
		b.commit();

		b.place(stickR2, 1, 3);
		b.clearRows();
		b.commit();

		assertEquals(3, b.getMaxHeight());
		checkColHeights(new int[]{3, 0, 3, 3, 2}, b);
		checkRowLengths(new int[]{4, 4, 3, 0, 0, 0}, b);
	}

	public void testDropHeight() {
		assertEquals(0, b.dropHeight(stickR2, 0));
		b.place(stickR2, 0, 0);
		b.commit();

		assertEquals(1, b.dropHeight(stickR2, 0));
		b.place(stickR2, 0, 0);
		b.commit();
	}

	public void testGetGrid() {
		assertTrue(b.getGrid(-1, 0));
		b.place(stickR2, 0, 0);
		b.commit();

		assertTrue(b.getGrid(0, 0));
		assertFalse(b.getGrid(0, 1));

	}

	// redo all tests with sanity check DEBAG  = true
	public void testSanityCheckCorrectCode() {
		b.setDebugMode(true);
		testCtor();

		b = new Board(5, 6);
		testPlaceReturnRowFilled();

		b = new Board(5, 6);
		testPlaceNoCommit();

		b = new Board(5, 6);
		testPlaceReturnBad();

		b = new Board(5, 6);
		testPlaceReturnOk();

		b = new Board(5, 6);
		testPlaceReturnOutOfBounds();

		b = new Board(5, 6);
		testClearRowsGaps();

		b = new Board(5, 6);
		testClearRowsBasic();

		b = new Board(5, 6);
		testClearRowsMultiple();

		b = new Board(5, 6);
		testDropHeight();

		b = new Board(5, 6);
		testGetGrid();
	}

	public void testSanityCheckBadBoard() {
		Board badBoard = new BadBoard(5, 6);
		badBoard.setDebugMode(true);


		Exception exception  = assertThrows(RuntimeException.class, () -> badBoard.place(stickR1, 0, 0));
		String expectedMessage = "Max Height is invalid. expected:";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}


	public void testSanityCheckBadBoard2() {
		Board badBoard = new BadBoard(5, 6);
		badBoard.setDebugMode(true);

		Exception exception  = assertThrows(RuntimeException.class, () -> badBoard.place(stickR1, 1, 0));
		String expectedMessage = "Widths array is invalid";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}


	public void testSanityCheckBadBoard3() {
		Board badBoard = new BadBoard(5, 6);
		badBoard.setDebugMode(true);

		Exception exception  = assertThrows(RuntimeException.class, () -> badBoard.place(stickR1, 0, 1));
		String expectedMessage = "Heights array is invalid";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}



	private void checkColHeights(int[] arr, Board b) {
		for (int i = 0; i < arr.length; i++) {
			assertEquals(arr[i], b.getColumnHeight(i));
		}

	}

	private void checkRowLengths(int[] arr, Board b) {
		for (int i = 0; i < arr.length; i++) {
			assertEquals(arr[i], b.getRowWidth(i));
		}
	}

}
