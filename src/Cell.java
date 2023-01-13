/** A Cell is one "square" on the field of a Mine Sweeper game. 
 * @author Andrew Merrill
 */

import java.awt.Color;

interface Cell {
    /**
     * Returns the row number of this cell.
     */
    public int getRow();

    /**
     * Returns the column number of this cell.
     */
    public int getCol();

    /**
     * Returns true if this cell is visible, meaning that its number of neighboring mines should be displayed.
     */
    public boolean isVisible();

    /**
     * Returns true if this cell contains a mine (regardless of whether it is visible or has been flagged).
     */
    public boolean isMine();

    /**
     * Returns true if this cell has been flagged (regardless of whether it is visible or contains a mine).
     */
    public boolean isFlagged();

    /**
     * Returns the number of mines that are in cells that are adjacent to this one.  Most cells have
     * eight neighbors (N, S, E, W, NE, NW, SE, SW).  Cells along the edges of the field or cells in the
     * corners have fewer neighbors.  Mines are counted regardless of whether they are visible
     * or flagged.  If there is a mine in the current cell, it is not counted.
     */
    public int getNeighborMines();

    /**
     * Gets the number to display on the grid
     * If the number of neighbors > 0, it returns the number of neighbors as a string
     * If the number of neighbors is 0, it returns an empty string
     * (Added by Alvin Zhang for graphics improvement.)
     */
    public String getNeighborsDisplay();

    /**
     * Gets the color to display with a number in a cell.
     * (Added by Alvin Zhang for graphics improvements)
     */
    public Color getNumberColor();
}
