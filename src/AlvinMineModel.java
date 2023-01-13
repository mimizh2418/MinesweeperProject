import java.util.ArrayList;

/** An {@code AlvinMineModel} is an implementation of the {@link MineModel} interface.
 * It performs all the game logic for the Minesweeper game, and stores and manages the
 * state of the game.
 * <p>References:
 * <a href="https://g.co/kgs/16HKp2">Google Minesweeper</a>,
 * <a href="https://minesweeperonline.com/#">Minesweeper Online</a></p>
 * @author Alvin Zhang
 */

public class AlvinMineModel implements MineModel{

    private int numRows, numCols, numMines, numFlags, numOpenCells;
    private long startTime;
    private boolean gameStarted, gameOver, playerDead, gameWon, generatedMines;
    private AlvinCell[][] grid;
    private boolean[][] restrictions;

    /** Checks if cell coordinates are within the bounds of the grid.
     * @param row the row of the coordinates to be tested
     * @param col the column of the coordinates to be tested
     * @return true if the coordinates are valid, false otherwise
     */
    private boolean isValidCell(int row, int col) {
        return (row >= 0 && col >= 0 && row < numRows && col < numCols);
    }

    /** Validates cell coordinates
     * @param row the row coordinate to be checked
     * @param col the column coordinate to be checked
     * @throws IllegalArgumentException if the coordinates are invalid
     */
    private void validateCellCoords(int row, int col) {
        if (!isValidCell(row, col)) throw new IllegalArgumentException("Invalid cell coordinates.");
    }

    /** Generates mines but never generates mines adjacent to the initial click.
     * If there are too many mines to avoid placing adjacent to the initial click, mines can be adjacent to but not on the cell of the initial click.
     * @param initialRow the row coordinate of the initial click
     * @param initialCol the column coordinate of the initial click
     * @throws IllegalArgumentException if the coordinates provided are invalid
     */
    private void generateMines(int initialRow, int initialCol) {
        validateCellCoords(initialRow, initialCol);
        // Set up restrictions for mine generation, first checking amount of mines to see if mines are to be generated adjacent to the initial click
        if (numRows * numCols - numMines >= 9) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (isValidCell(initialRow + i, initialCol + j)) restrictions[initialRow + i][initialCol + j] = true;
                }
            }
        } else {
            restrictions[initialRow][initialCol] = true;
        }
        // Randomly generate mine coordinates, retrying if the cell whose coordinates were generated was restricted or already a mine
        for (int mine = 0; mine < numMines; mine++) {
            int i, j;
            do {
                i = (int) (Math.random() * numRows);
                j = (int) (Math.random() * numCols);
            } while (grid[i][j].isMine() || restrictions[i][j]);
            grid[i][j].setMine();
            for (AlvinCell cell : getNeighboringCells(i, j)) cell.addNeighborMine();
        }
    }

    /** Gets all neighbor mines of a cell
     * @param row the row coordinate of the cell
     * @param col the column coordinate of the cell
     * @return an {@link ArrayList} of {@link AlvinCell}s that are the neighbors of the given cell.
     * @throws IllegalArgumentException if the coordinates provided are invalid
     */
    private ArrayList<AlvinCell> getNeighboringCells(int row, int col) {
        validateCellCoords(row, col);
        ArrayList<AlvinCell> out = new ArrayList<>();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (isValidCell(row + i, col + j) && !(i == 0 && j == 0)) out.add(grid[row+i][col+j]);
            }
        }
        return out;
    }

    // Shows all mines on the grid
    private void showAllMines() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (grid[i][j].isMine()) grid[i][j].show();
            }
        }
    }

    /** Starts a new game of Minesweeper. This method creates a field of the indicated size,
     * and initializes it. Mines are not placed until the location of the first click is known,
     * after which mines are generated ({@link #stepOnCell(int, int) stepOnCell}).
     * @param rows the number of rows for the field
     * @param cols the number of columns for the field
     * @param mines the number of mines to place in the field
     */
    @Override
    public void newGame(int rows, int cols, int mines) {
        if (mines >= rows * cols) throw new IllegalArgumentException("Too many mines for " + rows + " rows and " + cols + " cols.");
        if (rows <= 0 || cols <= 0 || mines <= 0) throw new IllegalArgumentException("Dimensions and number of mines must be positive.");
        numRows = rows;
        numCols = cols;
        numMines = mines;
        numFlags = 0;
        numOpenCells = 0;
        gameStarted = true;
        gameOver = false;
        playerDead = false;
        gameWon = false;
        generatedMines = false;
        grid = new AlvinCell[numRows][numCols];
        restrictions = new boolean[numRows][numCols];
        // Initialize Cell objects for the grid
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                grid[i][j] = new AlvinCell(i, j);
            }
        }
    }

    @Override
    public int getNumRows() {
        return numRows;
    }

    @Override
    public int getNumCols() {
        return numCols;
    }

    @Override
    public int getNumMines() {
        return numMines;
    }

    @Override
    public int getNumFlags() {
        return getNumMines() - numFlags;
    }

    @Override
    public int getElapsedSeconds() {
        if (generatedMines) return (int) ((System.currentTimeMillis() - startTime) / 1000.0);
        else return 0;
    }

    @Override
    public Cell getCell(int row, int col) {
        validateCellCoords(row, col);
        return grid[row][col];
    }

    /** Called when the player has stepped onto the cell at the given coordinates. If mines haven't been generated
     * (i.e. the first click of the game), generate mines first and ensure the location of the click is safe, and
     * has no neighboring mines. When the cell at the location of the click is empty, use a recursive flood-fill
     * algorithm to fill all connected empty cells, and their respective neighbors.
     * @param row the row number of the cell
     * @param col the column number of the cell
     * @throws IllegalArgumentException if the coordinates are invalid
     */
    @Override
    public void stepOnCell(int row, int col) {
        validateCellCoords(row, col);
        if (!generatedMines) {
            generateMines(row, col);
            startTime = System.currentTimeMillis();
            generatedMines = true;
        }
        if (!grid[row][col].isFlagged() && !grid[row][col].isVisible()) {
            grid[row][col].show();
            numOpenCells++;
            if (grid[row][col].isMine()) {
                playerDead = true;
                gameOver = true;
                showAllMines();
            }
            else if (grid[row][col].getNeighborMines() == 0) {
                for (AlvinCell cell : getNeighboringCells(row, col)) stepOnCell(cell.getRow(), cell.getCol());
            }
            if (numOpenCells == numRows * numCols - numMines && !playerDead) {
                gameOver = true;
                gameWon = true;
            }
        }
    }

    @Override
    public void chordCell(int row, int col) {
        validateCellCoords(row, col);
        if (grid[row][col].getNeighborFlags() == grid[row][col].getNeighborMines()) {
            for (AlvinCell cell : getNeighboringCells(row, col)) stepOnCell(cell.getRow(), cell.getCol());
        }
    }

    @Override
    public void placeOrRemoveFlagOnCell(int row, int col) {
        validateCellCoords(row, col);
        if (!grid[row][col].isVisible()) {
            if (grid[row][col].isFlagged()) {
                grid[row][col].toggleFlag();
                numFlags--;
                for (AlvinCell cell : getNeighboringCells(row,  col)) cell.removeNeighborFlag();
            }
            else if (getNumFlags() > 0) {
                grid[row][col].toggleFlag();
                numFlags++;
                for (AlvinCell cell : getNeighboringCells(row,  col)) cell.addNeighborFlag();
            }
        }
    }

    @Override
    public boolean isGameStarted() {
        return gameStarted;
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public boolean isPlayerDead() {
        return playerDead;
    }

    @Override
    public boolean isGameWon() {
        return gameWon;
    }
}
