import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** A MineView displays a graphical user interface for the Mine Sweeper game.
 * It relies on a separate MineModel to manage the state of the game itself.
 * @author Andrew Merrill
 * @author Alvin Zhang
 */

class MineView
{
    // set debug to true to see all cells, not just the visible ones
    private static final boolean debug = false;

    private final JFrame mainFrame;
    private final GamePanel gamePanel;
    private final ControlPanel controlPanel;
    private final MineModel mineModel;

    MineView(MineModel mineModel, int width, int height)
    {
        this.mineModel = mineModel;
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        gamePanel = new GamePanel();
        controlPanel = new ControlPanel();
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        mainFrame = new JFrame("Minesweeper");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().add(mainPanel);
        mainFrame.setSize(width, height);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    /** The ControlPanel is the panel with the New Game button and the text fields for configuring the game. */

    private class ControlPanel extends JPanel
    {
        private final JComboBox<String> difficulty;
        private final JLabel flagsLabel, timeLabel;
        private final javax.swing.Timer timer;

        ControlPanel()
        {
            setLayout(new FlowLayout());
            JButton newGameButton = new JButton("New Game");
            newGameButton.addActionListener(new NewGameListener());
            newGameButton.setMnemonic(KeyEvent.VK_N);
            add(newGameButton);
            add(new JLabel("Difficulty: "));
            difficulty = new JComboBox<>(new String[] {"Beginner", "Intermediate", "Expert"});
            difficulty.setSelectedIndex(2);
            add(difficulty);
            add(new JLabel("   Flags:"));
            flagsLabel = new JLabel("0    ");
            add(flagsLabel);
            timeLabel = new JLabel("0:00");
            add(new JLabel("Time:"));
            add(timeLabel);
            timer = new javax.swing.Timer(100, new TimerListener());
            timer.start();
        }

        /////////////////////////////////////////////////////////////////////////////////////////
        /** NewGameListener is used when the New Game button is clicked. */

        class NewGameListener implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
                int numRows, numCols, numMines;
                switch (difficulty.getSelectedIndex()) {
                    case 0 -> {
                        numRows = 9;
                        numCols = 9;
                        numMines = 10;
                        mainFrame.setSize(415, 475);
                    }
                    case 1 -> {
                        numRows = 13;
                        numCols = 15;
                        numMines = 40;
                        mainFrame.setSize(590, 575);
                    }
                    default -> {
                        numRows = 16;
                        numCols = 30;
                        numMines = 99;
                        mainFrame.setSize(950, 575);
                    }
                }
                mineModel.newGame(numRows, numCols, numMines);
                gamePanel.calculateCellSize();
                gamePanel.repaint();
            }
        }  // class NewGameListener

        /////////////////////////////////////////////////////////////////////////////////////////
        /** The timer is used to update the display of the current time and the number of flags placed. */

        class TimerListener implements ActionListener
        {
            private final java.text.DecimalFormat twoDigitFormat = new java.text.DecimalFormat("00");

            public void actionPerformed(ActionEvent event)
            {
                if (mineModel.isGameStarted() && ! mineModel.isGameOver())
                {
                    int elapsedTime = mineModel.getElapsedSeconds();
                    int minutes = elapsedTime / 60;
                    int seconds = elapsedTime % 60;
                    timeLabel.setText(minutes + ":" + twoDigitFormat.format(seconds));
                    flagsLabel.setText(mineModel.getNumFlags() + "");
                }
            }
        }
    } // class ControlPanel

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** The GamePanel is where the field is drawn on the screen. */

    private class GamePanel extends JPanel
    {
        private int panelHeight, panelWidth;  // size of the entire game panel
        private int cellSize;      // size of each cell
        private int offsetX, offsetY;         // offset between the northwest corner of the panel
        // and the northwest corner of the upper-left cell

        GamePanel()
        {
            addMouseListener(new MineMouseListener());
            addComponentListener(new GamePanelComponentListener());
        }

        /** Calculates the dimensions of the cells in the field of the game,
         * based on the size of the game panel, and the number of rows and columns in the field.
         * This method is called whenever the field is resized. (edited by Alvin Zhang to keep cells squares)
         */
        void calculateCellSize()
        {
            if (! mineModel.isGameStarted()) return;
            Dimension d = getSize();
            panelWidth = d.width;
            panelHeight = d.height;
            cellSize = Math.min(panelHeight / mineModel.getNumRows(), panelWidth / mineModel.getNumCols());
            offsetX = (panelWidth - cellSize *mineModel.getNumCols()) / 2;
            offsetY = (panelHeight - cellSize *mineModel.getNumRows()) / 2;
        }

        /** paintComponent re-draws the field */

        protected void paintComponent(Graphics pen)
        {
            // don't do any drawing if the game hasn't started yet
            if (! mineModel.isGameStarted()) {
                return;
            }

            // make the background black
            pen.setColor(Color.DARK_GRAY);
            pen.fillRect(0, 0, panelWidth, panelHeight);
            int numRows = mineModel.getNumRows();
            int numCols = mineModel.getNumCols();

            // draw every cell
            for (int r=0; r<numRows; r++) {
                for (int c=0; c<numCols; c++) {
                    Cell cell = mineModel.getCell(r,c);
                    drawCell(pen, cell);
                }
            }

        } // paintComponent()

        // Draws a gray cell with a border half white, half gray (Added by Alvin Zhang)
        private void drawDefaultCell(Graphics pen, int nw_x, int nw_y) {
            pen.setColor(Color.LIGHT_GRAY);
            pen.fillRect(nw_x, nw_y, cellSize, cellSize);
            int[] outline1_x = new int[] {nw_x, nw_x, nw_x+ cellSize, nw_x+7* cellSize /8, nw_x+ cellSize /8, nw_x+ cellSize /8};
            int[] outline1_y = new int[] {nw_y+ cellSize, nw_y, nw_y, nw_y+ cellSize /8, nw_y+ cellSize /8, nw_y+7* cellSize /8};
            int[] outline2_x = new int[] {nw_x+ cellSize, nw_x+ cellSize, nw_x, nw_x+ cellSize /8, nw_x+7* cellSize /8, nw_x+7* cellSize /8};
            int[] outline2_y = new int[] {nw_y, nw_y+ cellSize, nw_y+ cellSize, nw_y+7* cellSize /8, nw_y+7* cellSize /8, nw_y+ cellSize /8};
            pen.setColor(Color.WHITE);
            pen.fillPolygon(outline1_x, outline1_y, 6);
            pen.setColor(Color.GRAY);
            pen.fillPolygon(outline2_x, outline2_y, 6);
        }

        private void drawCell(Graphics pen, Cell cell)
        {
            // compute x and y coordinates of the north-west corner of the cell
            int nw_x = cell.getCol() * cellSize + offsetX;
            int nw_y = cell.getRow() * cellSize + offsetY;

            // draw a border around the cell
            pen.setColor(Color.GRAY);
            pen.drawRect(nw_x, nw_y, cellSize, cellSize);

            if (cell.isFlagged())
            {
                // draw a red flag on a gray
                drawDefaultCell(pen, nw_x, nw_y);
                int[] flag_x = new int[] {nw_x+cellSize/3, nw_x+cellSize/3, nw_x+2*cellSize/3, nw_x+ cellSize/3};
                int[] flag_y = new int[] {nw_y+5*cellSize/6, nw_y+ cellSize/6, nw_y+2*cellSize/6, nw_y+3*cellSize/6};
                pen.setColor(Color.RED);
                pen.fillPolygon(flag_x, flag_y, 4);
                pen.setColor(Color.BLACK);
                pen.drawPolygon(flag_x, flag_y, 4);
                if (mineModel.isGameOver() && !cell.isMine()) {
                    pen.setColor(new Color(153, 0, 0));
                    int[] x1_y = new int[] {nw_y+cellSize/6, nw_y+cellSize/6, nw_y+5* cellSize /6, nw_y+5*cellSize /6};
                    int[] x1_x = new int[] {nw_x+cellSize/6, nw_x+cellSize/3, nw_x+5*cellSize /6, nw_x+2*cellSize /3};
                    int[] x2_y = new int[] {nw_y+5*cellSize/6, nw_y+5*cellSize/6, nw_y+cellSize /6, nw_y+cellSize /6};
                    pen.fillPolygon(x1_x, x1_y, 4);
                    pen.fillPolygon(x1_x, x2_y, 4);
                }
            }
            else if (!debug && !cell.isVisible())
            {
                // unless we are debugging, draw non-visible squares using drawDefaultCell (edit by Alvin Zhang)
                drawDefaultCell(pen, nw_x, nw_y);
            }
            else if (cell.isMine())
            {
                // draw mines as dark red circles on a red background
                pen.setColor(Color.RED);
                pen.fillRect(nw_x, nw_y, cellSize, cellSize);
                pen.setColor(new Color(123, 0, 0));
                int radius = cellSize / 3;
                pen.fillOval(nw_x + cellSize / 2 - radius, nw_y + cellSize / 2 - radius, 2 * radius, 2 * radius);
            }
            else
            {
                // draw the number of adjacent mines on a light gray background
                //   (or a yellow background if we are debugging and this square is not yet visible)
                if (debug && !cell.isVisible()) pen.setColor(Color.YELLOW);
                else pen.setColor(Color.LIGHT_GRAY);
                pen.fillRect(nw_x+1, nw_y+1, cellSize, cellSize);
                pen.setColor(cell.getNumberColor());
                int fontSize = cellSize * 21/32;
                pen.setFont(new Font("Courier New", Font.BOLD, fontSize));
                pen.drawString(cell.getNeighborsDisplay(), nw_x + 3* cellSize /7, nw_y + 5* cellSize /7);
            }

        }  // drawCell()

        /////////////////////////////////////////////////////////////////////////////////////////

        class MineMouseListener implements MouseListener
        {
            public void mousePressed(MouseEvent event)
            {
                if (!mineModel.isGameStarted() || mineModel.isGameOver()) return;
                int x = event.getX();
                int y = event.getY();
                if ( x < offsetX || y < offsetY) return;

                int row = (y-offsetY) / cellSize;
                int col = (x-offsetX) / cellSize;
                if (row < 0 || row >= mineModel.getNumRows() || col < 0 || col >= mineModel.getNumCols()) return;

                int button = event.getButton();
                if (button == MouseEvent.BUTTON3 || event.isShiftDown()) // right-click or shift-click
                {
                    mineModel.placeOrRemoveFlagOnCell(row, col);
                }
                else if (button == MouseEvent.BUTTON1)      // left-click, chords cell if it is visible, otherwise steps on it.
                {
                    if (!mineModel.getCell(row, col).isVisible()) mineModel.stepOnCell(row, col);
                    else mineModel.chordCell(row, col);
                }

                repaint();

                if (mineModel.isPlayerDead()) {
                    JOptionPane.showMessageDialog(mainFrame, "Boom!  You just stepped on a mine!  You're dead!");
                }
                else if (mineModel.isGameWon()) {
                    JOptionPane.showMessageDialog(mainFrame, "Congratulations!  You won the game!");
                }
            }

            public void mouseClicked(MouseEvent event) { }
            public void mouseReleased(MouseEvent event) { }
            public void mouseEntered(MouseEvent event) { }
            public void mouseExited(MouseEvent event) { }
        }  // class MineMouseListener

        //////////////////////////////////////////////////////////////////////////////////////////
        class GamePanelComponentListener extends ComponentAdapter
        {
            public void componentResized(ComponentEvent event)
            {
                // when the game panel is resized, adjust the size of the cells
                calculateCellSize();
                repaint();
            }
        } // class GamePanelComponentListener
        /////////////////////////////////////////////////////////////////////////////////////////

    } // class GamePanel

} // class MineView

