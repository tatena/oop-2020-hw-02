// Board.java

import java.util.Arrays;

import static com.sun.tools.javac.jvm.ByteCodes.swap;

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	protected boolean[][] grid;
	private boolean DEBUG = true;
	boolean committed;
	protected int[] heightsArr;
	protected int[] widthsArr;
	private int maxHeight;

	//backups
	private boolean[][] gridBackup;
	private int[] heightsArrBackup;
	private int[] widthsArrBackup;
	private int maxHeightBackup;
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		committed = true;
		heightsArr = new int[width];
		Arrays.fill(heightsArr, 0);
		widthsArr = new int[height];
		Arrays.fill(widthsArr, 0);
		maxHeight = 0;

		//backups
		gridBackup = new boolean[width][height];
		heightsArrBackup = new int[width];
		Arrays.fill(heightsArrBackup, 0);
		widthsArrBackup = new int[height];
		Arrays.fill(widthsArrBackup, 0);
		maxHeightBackup = 0;
	}
	
	
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() { return width; }
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() { return height; }
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() { return maxHeight; }
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (!DEBUG)
			return;
		int[] wArr =  new int[height];
		int[] hArr = new int[width];
		int maxH = Integer.MIN_VALUE;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (grid[i][j]) {
					hArr[i] = j + 1;
					wArr[j]++;
				}
				maxH = Math.max(maxH, hArr[i]);
			}
		}
		if (maxH != getMaxHeight())
			throw new RuntimeException("Max Height is invalid. expected: " + maxH + "; got: " + maxHeight );

		if (!Arrays.equals(wArr, widthsArr))
			throw new RuntimeException("Widths array is invalid");

		if (!Arrays.equals(hArr, heightsArr))
			throw new RuntimeException("Heights array is invalid");
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int[] skirt = piece.getSkirt();
		int res = Integer.MIN_VALUE;
		for (int i = 0;  i < skirt.length; i++) {
			res = Math.max(res, heightsArr[x + i] - skirt[i]);
		}
		return res;
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		return heightsArr[x];
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		 return widthsArr[y];
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		return !inBounds(x, y) || grid[x][y];
	}
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");
		saveState();
		committed = false;

		int boardX, boardY;
		int res = PLACE_OK;
		for (TPoint pt : piece.getBody()) {
			boardX = x + pt.x;
			boardY = y + pt.y;
			if (!inBounds(boardX, boardY))
				return PLACE_OUT_BOUNDS;
			if (grid[boardX][boardY])
				return PLACE_BAD;
			grid[boardX][boardY] = true;
			heightsArr[boardX] = Math.max (heightsArr[boardX], boardY + 1);
			maxHeight = Math.max(maxHeight, heightsArr[boardX]);
			if (++widthsArr[boardY] == width) {
				res =  PLACE_ROW_FILLED;
			}
		}
		sanityCheck();
		return res;
	}

	private void saveState() {
		maxHeightBackup  = maxHeight;
		System.arraycopy(heightsArr, 0, heightsArrBackup, 0, width);
		System.arraycopy(widthsArr, 0, widthsArrBackup, 0, height);
		for (int i = 0; i < width; i++) {
			System.arraycopy(grid[i], 0, gridBackup[i], 0, height);
		}
	}


	private boolean inBounds(int x, int y) {
		return x > -1 && x < width && y > -1 && y < height;
	}


	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		if (committed)
			saveState();
		committed = false;
		int rowsCleared = 0;
		int toRow = 0;
		for (int i=0; i<maxHeight; i++) {
			if (getRowWidth(i) != width) {
				copyRow (toRow, i);
				widthsArr[toRow] = getRowWidth(i);
				toRow++;
			} else {
				rowsCleared ++;
			}
		}
		int oldMaxHeight = maxHeight;
		adjustHeightsArr(rowsCleared);
		adjustFalseRows(oldMaxHeight - maxHeight);
		sanityCheck();
		return rowsCleared;
	}

	private void adjustHeightsArr(int rowsCleared) {
		maxHeight = Integer.MIN_VALUE;
		for (int i = 0; i<heightsArr.length; i++) {
			heightsArr[i] -= rowsCleared;
			while (heightsArr[i]  - 1 >= 0 && !grid[i][heightsArr[i] - 1]){
				heightsArr[i]--;
			}
			maxHeight = Math.max(maxHeight, heightsArr[i]);
		}
	}

	private void adjustFalseRows(int rowsCleared) {
		for (int i = 0; i < rowsCleared; i++) {
			widthsArr[maxHeight + i] = 0;
			for (int j =0; j < width; j++)
				grid[j][maxHeight + i] = false;
		}
	}

	private void copyRow(int toRow, int fromRow) {
	//	System.out.println("to -> " + toRow + " from -> " + fromRow);
		for (int i = 0; i < width; i++) {
			grid[i][toRow] = grid[i][fromRow];
		}
	}


	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		if (committed)
			return;

		Object temp = grid;
		grid = gridBackup;
		gridBackup = (boolean[][])temp;

		temp = heightsArr;
		heightsArr = heightsArrBackup;
		heightsArrBackup = (int[])temp;

		temp = widthsArr;
		widthsArr = widthsArrBackup;
		widthsArrBackup = (int[])temp;

		temp = maxHeight;
		maxHeight = maxHeightBackup;
		maxHeightBackup = maxHeight;

		committed = true;
		sanityCheck();
	}


	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		committed = true;
	}

	
	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}

	public void setDebugMode(boolean b) {
		DEBUG = b;
	}

}


