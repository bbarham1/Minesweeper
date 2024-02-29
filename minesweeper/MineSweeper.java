import java.util.Random;

/**
 * The implementation of a Minesweeper game.
 * 
 * @author basel barham
 **/
public class MineSweeper {

	/**
	 * levels that can be played, supported game levels.
	 */
	public enum Level {
		/**
		 * the statuse values that the game can have.
		 */
		TINY, EASY, MEDIUM, HARD, CUSTOM
	}

	// each level has a different board size (number of rows/columns)
	// and a different number of mines

	/**
	 * # of rows in easy game.
	 */
	private static int ROWS_EASY = 9;
	/**
	 * # of col in easy game.
	 */
	private static int COLS_EASY = 9;
	/**
	 * # of total mines in easy game.
	 */
	private static int MINES_EASY = 10;

	/**
	 * # of total rows in tiny game.
	 */
	private static int ROWS_TINY = 5;
	/**
	 * # of total cols in tiny game.
	 */
	private static int COLS_TINY = 5;
	/**
	 * # of total mines in tiny game.
	 */
	private static int MINES_TINY = 3;

	/**
	 * # of rows in medium game.
	 */
	private static int ROWS_MEDIUM = 16;
	/**
	 * # of cols in medium game.
	 */
	private static int COLS_MEDIUM = 16;
	/**
	 * # of total mines in medium game.
	 */
	private static int MINES_MEDIUM = 40;

	/**
	 * # of rows in hard game.
	 */
	private static int ROWS_HARD = 16;
	/**
	 * # of cols in hard game.
	 */
	private static int COLS_HARD = 30;
	/**
	 * # of total mines in hard game.
	 */
	private static int MINES_HARD = 99;

	/**
	 * The 2d board of cells.
	 */
	private DynGrid310<Cell> board;

	/**
	 * Number of rows of the board.
	 */
	private int rowCount;

	/**
	 * Number of columns of the board.
	 */
	private int colCount;

	/**
	 * Number of mines in the board.
	 */
	private int mineTotalCount;

	/**
	 * Number of cells clicked / exposed.
	 */
	private int clickedCount;

	/**
	 * number of cells flagged as a mine.
	 */
	private int flaggedCount;

	/**
	 * Game possible status.
	 */
	public enum Status {
		/**
		 * the values that game can have.
		 */
		INIT, INGAME, EXPLODED, SOLVED
	}

	/**
	 * private status.
	 */
	private Status status;

	/**
	 * string names of status.
	 */
	public final static String[] Status_STRINGS = { "INIT", "IN_GAME", "EXPLODED", "SOLVED" };

	/**
	 * constructor, initialize game based on a provided seed for random numbers and
	 * the specified level.
	 * 
	 * @param seed  seed
	 * @param level level
	 */
	public MineSweeper(int seed, Level level) {

		// if level is customized, need more details (number of rows/columns/mines)
		if (level == Level.CUSTOM)
			throw new IllegalArgumentException("Customized games need more parameters!");

		// set number of rows, columns, mines based on the pre-defined levels
		switch (level) {
            case TINY:
                rowCount = ROWS_TINY;
                colCount = COLS_TINY;
                mineTotalCount = MINES_TINY;
                break;
            case EASY:
                rowCount = ROWS_EASY;
                colCount = COLS_EASY;
                mineTotalCount = MINES_EASY;
                break;
            case MEDIUM:
                rowCount = ROWS_MEDIUM;
                colCount = COLS_MEDIUM;
                mineTotalCount = MINES_MEDIUM;
                break;
            case HARD:
                rowCount = ROWS_HARD;
                colCount = COLS_HARD;
                mineTotalCount = MINES_HARD;
                break;
            default:
                // should not be able to reach here!
                rowCount = ROWS_TINY;
                colCount = COLS_TINY;
                mineTotalCount = MINES_TINY;
		}

		// create an empty board of the needed size
		
		board = genEmptyBoard(rowCount, colCount);

		// place mines, and initialize cells
		
		initBoard(seed);
	}

	/**
	 * constructor: should only be used for customized games.
	 * 
	 * @param seed      seed
	 * @param level     level
	 * @param rowCount  rowCount
	 * @param colCount  colCount
	 * @param mineCount mineCount
	 */
	public MineSweeper(int seed, Level level, int rowCount, int colCount, int mineCount) {

		if (level != Level.CUSTOM)
			throw new IllegalArgumentException("Only customized games need more parameters!");

		// set number of rows/columns/mines
		// assume all numbers are valid (check MineGUI for additional checking code)
		this.rowCount = rowCount;
		this.colCount = colCount;
		this.mineTotalCount = mineCount;

		// create an empty board of the needed size: you implement this method
		board = genEmptyBoard(rowCount, colCount);

		// place mines, and initialize cells: you implement part of this method
		initBoard(seed);
	}

	/**
	 * method to initialize the game, including placing mines. assume it is invoked
	 * only after an empty board (rowCount x colCount) has been created and set
	 * 
	 * @param seed seed
	 */
	public void initBoard(int seed) {

		// use seed to initialize a random number sequence
		Random random = new Random(seed);

		// randomly place mines on board
		int mineNum = 0;
		for (; mineNum < mineTotalCount;) {

			// generate next (row, col)
			int row = random.nextInt(rowCount);
			int col = random.nextInt(colCount);

			// cell already has a mine: try again
			if (hasMine(row, col)) {
				continue;
			}

			// place mine
			board.get(row, col).setMine();
			mineNum++;
		}

		// calculate nbr counts for each cell
		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < colCount; col++) {

				// TODO: you implement countNbrMines()
				int count = countNbrMines(row, col);
				board.get(row, col).setCount(count);
			}
		}

		// initialize other game settings
		status = Status.INIT;

		flaggedCount = 0;
		clickedCount = 0;

	}

	/**
	 * method reports number of rows.
	 * 
	 * @return rowCount
	 */
	public int rowCount() {
		return rowCount;
	}

	/**
	 * method reports number of columns.
	 * 
	 * @return colCount
	 */
	public int colCount() {
		return colCount;
	}

	/**
	 * method reports whether board is solved.
	 * 
	 * @return status == Status.SOLVED
	 */
	public boolean isSolved() {
		return status == Status.SOLVED;
	}

	/**
	 * method reports whether a mine has exploded.
	 * 
	 * @return status == Status.EXPLODED
	 */
	public boolean isExploded() {
		return status == Status.EXPLODED;
	}

	/**
	 * method displays board, use this for debugging.
	 * 
	 * @return sb
	 */
	public String boardToString() {
		StringBuilder sb = new StringBuilder();

		// header of column indexes
		sb.append("- |");
		for (int j = 0; j < board.getNumCol(); j++) {
			sb.append(j + "|");
		}
		sb.append("\n");

		for (int i = 0; i < board.getNumRow(); i++) {
			sb.append(i + " |");
			for (int j = 0; j < board.getNumCol(); j++) {
				sb.append(board.get(i, j).toString());
				sb.append("|");
			}
			sb.append("\n");
		}
		return sb.toString().trim();

	}

	/**
	 * method displays the game status and board, use this for debugging.
	 * 
	 * @return sb
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Board Size: " + rowCount() + " x " + colCount() + "\n");
		sb.append("Total mines: " + mineTotalCount + "\n");
		sb.append("Remaining mines: " + mineLeft() + "\n");
		sb.append("Game status: " + getStatus() + "\n");

		sb.append(boardToString());
		return sb.toString().trim();
	}



	/**
	 * method to report if cell flagged. return true if cell at (row,col) is
	 * flagged, false otherwise
	 * 
	 * @param row row
	 * @param col col
	 * @return true or false
	 */
	public boolean isFlagged(int row, int col) {

		// return false for invalid cell indexes
		if (!board.isValidCell(row, col)) {
			return false;
		}

		Cell cell = board.get(row, col);
		return (cell.isFlagged());
	}

	/**
	 * method to report if cell isn't hidden. return true if cell at (row,col) is
	 * not hidden, false otherwise
	 * 
	 * @param row row
	 * @param col col
	 * @return true or false
	 */
	public boolean isVisible(int row, int col) {

		// return false for invalid cell indexes
		if (!board.isValidCell(row, col)) {
			return false;
		}

		Cell cell = board.get(row, col);
		return (cell.visible());
	}

	/**
	 * method to report if cell has mine. return true if cell at (row,col) has a
	 * mine, regardless whether it has been flagged or not; false otherwise
	 * 
	 * @param row row
	 * @param col col
	 * @return true or false
	 */
	public boolean hasMine(int row, int col) {

		// return false for invalid cell indexes
		if (!board.isValidCell(row, col)) {
			return false;
		}

		Cell cell = board.get(row, col);
		return (cell.hasMine());
	}

	/**
	 * method that returns the count associated with cell at (row,col) has a mine.
	 * 
	 * @param row row
	 * @param col col
	 * @return -2, cell.getCount()
	 */
	public int getCount(int row, int col) {

		// return -2 for invalid cell indexes
		if (!board.isValidCell(row, col)) {
			return -2;
		}

		Cell cell = board.get(row, col);
		return (cell.getCount());
	}

	
	/**
	 * report how many mines have not be flagged.
	 * 
	 * @return unflagged mines
	 */
	public int mineLeft() {
		return mineTotalCount - flaggedCount;
	}

	/**
	 * report current game status.
	 * 
	 * @return status
	 */
	public String getStatus() {
		return Status_STRINGS[status.ordinal()];
	}



	/**
	 * method that returns the game board.
	 * 
	 * @return board
	 */
	public DynGrid310<Cell> getBoard() {
		return board;
	}

	/**
	 * set game board.
	 * 
	 * @param newBoard  newBoard
	 * @param mineCount mineCount
	 */
	public void setBoard(DynGrid310<Cell> newBoard, int mineCount) {
		// set board
		this.board = newBoard;

		// set size
		rowCount = board.getNumRow();
		colCount = board.getNumCol();

		// set other features
		status = Status.INIT;

		flaggedCount = 0;
		clickedCount = 0;
		mineTotalCount = mineCount;
	}


	/**
	 * method to create and return a grid with rowNum x colNum individual cells in
	 * it. all cells are default cell objects (no mines) amortized O(rowCount x
	 * colCount)
	 * 
	 * @param rowNum rowNum
	 * @param colNum colNum
	 * @return newBoard
	 */
	public static DynGrid310<Cell> genEmptyBoard(int rowNum, int colNum) {

		// if rowNum or colNum is not positive, return null
		if (rowNum < 0 || colNum < 0) {
			return null;
		}
		DynGrid310<Cell> newBoard = new DynGrid310<>();

		// Initialize the game board with empty cells (no mines)
		for (int i = 0; i < rowNum; i++) {
			DynArr310<Cell> row = new DynArr310<>();
			for (int j = 0; j < colNum; j++) {
				// Create and add an empty Cell to the row
				row.add(new Cell());
			}
			// Add the row to the game board
			newBoard.addRow(newBoard.getNumRow(), row);
		}

		return newBoard;
	}

	/**
	 * method that counts number of mines in the neighbor cells of cell (row, col).
	 * 
	 * @param row row
	 * @param col col
	 * @return -1, -1, or countMine
	 */
	public int countNbrMines(int row, int col) {

		// return -2 for invalid row / col indexes
		if (board.isValidCell(row, col) == false) {
			return -2;
		}
		// return -1 if cell at (row, col) has a mine underneath it
		if (hasMine(row, col) == true) {
			return -1;
		}
		int countMine = 0;
		if (board.isValidCell(row - 1, col - 1) && hasMine(row - 1, col - 1)) {
			countMine++;
		}
		if (board.isValidCell(row - 1, col) && hasMine(row - 1, col)) {
			countMine++;
		}
		if (board.isValidCell(row - 1, col + 1) && hasMine(row - 1, col + 1)) {
			countMine++;
		}
		if (board.isValidCell(row, col - 1) && hasMine(row, col - 1)) {
			countMine++;
		}
		if (hasMine(row, col + 1) && board.isValidCell(row, col + 1)) {
			countMine++;
		}
		if (board.isValidCell(row + 1, col - 1) && hasMine(row + 1, col - 1)) {
			countMine++;
		}
		if (board.isValidCell(row + 1, col + 1) && hasMine(row + 1, col + 1)) {
			countMine++;
		}
		if (board.isValidCell(row + 1, col) && hasMine(row + 1, col)) {
			countMine++;
		}
		return countMine;
	}

	// ******************************************************
	// ******* Methods to support game operations *******
	// ******************************************************

	/**
	 * method to open cell located at (row,col).
	 * 
	 * 
	 * @param row row
	 * @param col col
	 * @return -2, -1, clickedAt.getCount()
	 */
	public int clickAt(int row, int col) {

		// for an invalid cell location, no change and return -2
		if (board.isValidCell(row, col) == false) {
			return -2;
		}

		Cell clickedAt = board.get(row, col);
		status = Status.INGAME;
		// for a valid cell location, no change if cell is already flagged or exposed,
		// return -2
		if (clickedAt.isFlagged() || clickedAt.visible()) {
			return -2;
		}
		// if cell has a mine, open it would explode the mine, update game status
		// accordingly and return -1
		if (clickedAt.hasMine()) {
			status = Status.EXPLODED;
			clickedAt.setVisible();
			return -1;
		}

		// - otherwise, open this cell and return number of mines adjacent to it
		// - if the cell is not adjacent to any mines (i.e. a zero-count cell),
		// also open all zero-count cells that are connected to this cell,
		// as well as all cells that are orthogonally or diagonally adjacent
		// to those zero-count cells.
		// - HINT: recursion can really help! Consider define private helper methods.
		// - update game status as needed
		// - update other game features as needed

		clickedAt.setVisible();
		clickedCount++;

		if (clickedAt.getCount() == 0) {

			if (board.isValidCell(row - 1, col - 1) && !board.get(row - 1, col - 1).visible()) {
				clickAt(row - 1, col - 1);
			}
			if (board.isValidCell(row - 1, col) && !board.get(row - 1, col).visible()) {
				clickAt(row - 1, col);
			}
			if (board.isValidCell(row - 1, col + 1) && !board.get(row - 1, col + 1).visible()) {
				clickAt(row - 1, col + 1);
			}
			if (board.isValidCell(row, col - 1) && !board.get(row, col - 1).visible()) {
				clickAt(row, col - 1);
			}
			if (board.isValidCell(row, col + 1) && !board.get(row, col + 1).visible()) {
				clickAt(row, col + 1);
			}
			if (board.isValidCell(row + 1, col - 1) && !board.get(row + 1, col - 1).visible()) {
				clickAt(row + 1, col - 1);
			}
			if (board.isValidCell(row + 1, col + 1) && !board.get(row + 1, col + 1).visible()) {
				clickAt(row + 1, col + 1);
			}
			if (board.isValidCell(row + 1, col) && !board.get(row + 1, col).visible()) {
				clickAt(row + 1, col);
			}
		}
		if (clickedCount == (rowCount * colCount) - mineTotalCount) {
			status = Status.SOLVED;
		}
		return clickedAt.getCount();
	}

	/**
	 * method to flag at cell located at (row,col), return whether the cell is
	 * flagged or not. O(1)
	 * 
	 * @param row row
	 * @param col col
	 * @return true or false
	 */
	public boolean flagAt(int row, int col) {

		// return false for an invalid cell location or cell already open
		if (board.isValidCell(row, col) == false || board.get(row, col).visible()) {
			return false;
		}

		// otherwise, flag the cell as needed and update relevant game features
		// update game status as needed
		if (!board.get(row, col).isFlagged()) {
			board.get(row, col).setFlagged();
			flaggedCount++;
		}
		return true;
	}

	/**
	 * method to Un-flag at cell located at (row,col). return whether the cell is
	 * updated from flagged to unflagged
	 * 
	 * @param row row
	 * @param col col
	 * @return true or false
	 */
	public boolean unFlagAt(int row, int col) {

		// return false for an invalid cell location or no change if cell is not flagged
		// before
		if (board.isValidCell(row, col) == false || board.get(row, col).visible()) {
			return false;
		}
		// otherwise, unflag the cell and update relevant game features
		if (board.get(row, col).isFlagged()) {
			board.get(row, col).unFlagged();
			flaggedCount--;
			return true;
		}
		return false;
	}

	// ******************************************************
	// ******* BELOW THIS LINE IS TESTING CODE *******
	// ******* Edit it as much as you'd like! *******
	// ******* Remember to add JavaDoc *******
	// ******************************************************

	/**
	 * This method is for testing code.
	 * 
	 * @param args args
	 */
	public static void main(String args[]) {
		// basic: get an empty board with no mines
		DynGrid310<Cell> myBoard = MineSweeper.genEmptyBoard(3, 4);
		// System.out.println(myBoard);

		// board size, all 12 cells should be in the default state, no mines
		if (myBoard.getNumRow() == 3 && myBoard.getNumCol() == 4 && !myBoard.get(0, 0).hasMine()
				&& !myBoard.get(1, 3).visible() && !myBoard.get(2, 2).isFlagged()
				&& myBoard.get(2, 1).getCount() == -1) {
			System.out.println("Yay 0");
		}

		// init a game at TINY level
		// use the same random number sequence as GUI -
		// this will create the same board as Table 2 of p1 spec PDF.
		// you can change this for your own testing.

		Random random = new Random(10);

		MineSweeper game = new MineSweeper(random.nextInt(), Level.TINY);
		// System.out.println("hello i am here" );
		// print out the initial board and verify game setting
		// System.out.println(game);
		// expected board:
		// - |0|1|2|3|4|
		// 0 |?|?|?|?|?|
		// 1 |?|?|?|?|?|
		// 2 |?|?|?|?|?|
		// 3 |?|?|?|?|?|
		// 4 |?|?|?|?|?|

		// countNbrMines
		// System.out.println(game.countNbrMines(0,0));
		// System.out.println(game.countNbrMines(4,2));
		// System.out.println(game.countNbrMines(3,3));
		// System.out.println(game.countNbrMines(2,3));
		// System.out.println(game.countNbrMines(5,5));

		if (game.countNbrMines(0, 0) == 0 && game.countNbrMines(4, 2) == 1 && game.countNbrMines(3, 3) == 3
				&& game.countNbrMines(2, 3) == -1 && game.countNbrMines(5, 5) == -2) {
			System.out.println("Yay 1");
		}
		// System.out.println("hello i am here" );
		// first click at (3,3)
		if (game.clickAt(-1, 0) == -2 && game.clickAt(3, 3) == 3 && game.isVisible(3, 3) && !game.isVisible(0, 0)
				&& game.getStatus().equals("IN_GAME") && game.mineLeft() == 3) {
			System.out.println("Yay 2");
		}
		// expected board:
		// - |0|1|2|3|4|
		// 0 |?|?|?|?|?|
		// 1 |?|?|?|?|?|
		// 2 |?|?|?|?|?|
		// 3 |?|?|?|3|?|
		// 4 |?|?|?|?|?|

		// click at a mine cell

		if (game.clickAt(2, 3) == -1 && game.isVisible(2, 3) && game.getStatus().equals("EXPLODED")) {
			System.out.println("Yay 3");
		}
		// expected board:
		// - |0|1|2|3|4|
		// 0 |?|?|?|?|?|
		// 1 |?|?|?|?|?|
		// 2 |?|?|?|X|?|
		// 3 |?|?|?|3|?|
		// 4 |?|?|?|?|?|
		// start over with the same board
		random = new Random(10);
		game = new MineSweeper(random.nextInt(), Level.TINY);
		game.clickAt(3, 3);

		// flag and unflag

		// System.out.println(game.flagAt(2,3));
		// System.out.println(!game.isVisible(2,3));
		// System.out.println(game.isFlagged(2,3));
		// System.out.println(game.flagAt(2,4) );
		// System.out.println(game.mineLeft());
		// System.out.println(game.unFlagAt(2,3));
		// System.out.println(!game.isFlagged(2,3) );
		// System.out.println(game.mineLeft());
		if (game.flagAt(2, 3) && !game.isVisible(2, 3) && game.isFlagged(2, 3) && game.flagAt(2, 4)
				&& game.mineLeft() == 1 && game.unFlagAt(2, 3) && !game.isFlagged(2, 3) && game.mineLeft() == 2) {
			System.out.println("Yay 4");
		}

		// cell state & operations
		// - a flagged cell can not be clicked
		// - flag a cell already flagged does not change anything but still returns true
		// - an opened cell cannot be flagged or unflagged
		// - a hidden cell not flagged cannot be unflagged

		if (game.clickAt(2, 4) == -2 && game.flagAt(2, 4) && !game.flagAt(3, 3) && !game.unFlagAt(3, 3)
				&& !game.unFlagAt(2, 3)) {
			System.out.println("Yay 5");
		}

		if (game.clickAt(0, 0) == 0 && game.isVisible(0, 0) && game.isVisible(4, 0) && game.isVisible(0, 4)
				&& game.isVisible(3, 2) && !game.isVisible(3, 4) && !game.isVisible(4, 3)) {
			System.out.println("Yay 6");
		}
		// expected board:
		// - |0|1|2|3|4|
		// 0 | | | | | |
		// 1 | | |1|2|2|
		// 2 | | |1|?|F|
		// 3 | | |2|3|?|
		// 4 | | |1|?|?|

		// open all none-mine cells without any explosion solve the game!

		if (game.clickAt(4, 4) == 1 && game.clickAt(3, 4) == 3 && game.getStatus().equals("SOLVED")) {
			System.out.println("Yay 7");
		}
		// expected board:
		// - |0|1|2|3|4|
		// 0 | | | | | |
		// 1 | | |1|2|2|
		// 2 | | |1|?|F|
		// 3 | | |2|3|3|
		// 4 | | |1|?|1|
	}

}