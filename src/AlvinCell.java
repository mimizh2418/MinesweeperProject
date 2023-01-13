import java.awt.Color;

/** An {@code AlvinCell} is an implementation of the {@link Cell} interface.
 * This is a data storage object that stores the position and state of a cell in Minesweeper.
 * An {@code AlvinCell} does not perform any game logic; logic is handled by the {@link AlvinMineModel} class.
 * @author Alvin Zhang
 */

public class AlvinCell implements Cell {

    private int neighbors, neighborFlags;
    private final int row, col;
    private boolean open, flagged, mine;

    /** Creates a new {@code AlvinCell}.
     * @param row The row that this cell is on
     * @param col The column that this cell is on
     */
    public AlvinCell(int row, int col) {
        this.row = row;
        this.col = col;
        neighbors = 0;
    }

    /** Gets the row coordinate of this {@code AlvinCell}. */
    @Override
    public int getRow() {
        return row;
    }

    /** Gets the column coordinate of this {@code AlvinCell}. */
    @Override
    public int getCol() {
        return col;
    }

    /** Uncovers this cell, making it visible. */
    public void show() {
        open = true;
    }

    /** Checks if this cell is visible or not
     * @return {@code true} if this cell is visible, {@code false} otherwise
     */
    @Override
    public boolean isVisible() {
        return open;
    }

    /** Sets this cell to be a mine */
    public void setMine() {
        mine = true;
    }

    /** Checks if this cell is a mine.
     * @return {@code true} if this cell is a mine, {@code false} otherwise
     */
    @Override
    public boolean isMine() {
        return mine;
    }

    /** Toggles the flag on this cell, making it flagged if it isn't, and not flagged if it is.
     * Does not flag visible cells
     */
    public void toggleFlag() {
        if (!isVisible()) flagged = !flagged;
    }

    /** Checks if this cell is flagged or not
     * @return {@code true} if this cell is flagged, {@code false} otherwise
     */
    @Override
    public boolean isFlagged() {
        return flagged;
    }

    /** Adds 1 to the number of neighboring mines. */
    public void addNeighborMine() {
        neighbors++;
    }

    /** Checks the number of neighboring mines this cell has.
     * @return the number of adjacent mines
     */
    @Override
    public int getNeighborMines() {
        return neighbors;
    }

    /** Adds 1 to the number of neighboring flags */
    public void addNeighborFlag() {
        neighborFlags++;
    }

    /** Subtracts 1 from the number of the neighboring flags. */
    public void removeNeighborFlag() {
        neighborFlags--;
    }

    /** Get the number of adjacent flags.
     * @return the number of neighboring flags.
     */
    public int getNeighborFlags() {
        return neighborFlags;
    }

    /** Gets the number that should be displayed when this cell is opened
     * @return an empty string if this cell has no neighbor mines or if this cell is a mine, otherwise, return a string
     * of the number of neighbor mines.
     */
    @Override
    public String getNeighborsDisplay() {
        if (neighbors == 0 || isMine()) return "";
        else return Integer.toString(neighbors);
    }

    /** Gets the color that the graphics engine should color the number displayed in this cell when it is opened.
     * @return a {@link Color} object that represents the color of the number displayed with this cell
     */
    @Override
    public Color getNumberColor() {
        return switch (neighbors) {
            case 1 -> Color.BLUE;
            case 2 -> new Color(0, 123, 0);
            case 3 -> Color.RED;
            case 4 -> new Color(102, 0, 153);
            case 5 -> new Color(153, 0, 0);
            case 6 -> Color.CYAN;
            case 7 -> Color.BLACK;
            case 8 -> Color.GRAY;
            default -> Color.WHITE;
        };
    }
}
