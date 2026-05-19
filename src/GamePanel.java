import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements MouseListener {

    private GameFrame frame;

    private final int SIZE = 8;
    private final int CELL_SIZE = 60;
    private final int BOARD_X = 60;
    private final int BOARD_Y = 90;

    private int[][] board = new int[SIZE][SIZE];

    private int score = 0;
    private int moves = 20;

    private Point selected = null;

    private Random random = new Random();

    private boolean isAnimating = false;
    private ArrayList<Point> removingCandies = new ArrayList<>();
    private float animationScale = 1.0f;

    private Color[] candyColors = {
            new Color(240, 70, 70),
            new Color(70, 130, 240),
            new Color(70, 200, 90),
            new Color(245, 210, 60),
            new Color(170, 80, 220),
            new Color(255, 140, 50)
    };

    public GamePanel(GameFrame frame, boolean newGame) {
        this.frame = frame;

        setLayout(null);
        setBackground(new Color(250, 245, 255));
        addMouseListener(this);

        JButton menuButton = new JButton("Menu");
        menuButton.setBounds(30, 620, 100, 35);
        add(menuButton);

        JButton restartButton = new JButton("Restart");
        restartButton.setBounds(240, 620, 100, 35);
        add(restartButton);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(450, 620, 100, 35);
        add(saveButton);

        menuButton.addActionListener(e -> {
            saveGame();
            frame.showMenu();
        });

        restartButton.addActionListener(e -> {
            startNewGame();
            repaint();
        });

        saveButton.addActionListener(e -> {
            saveGame();
            JOptionPane.showMessageDialog(this, "Game saved!");
        });

        if (newGame) {
            startNewGame();
        } else {
            continueGame();
        }
    }

    private void startNewGame() {
        score = 0;
        moves = 20;
        selected = null;
        createBoard();
    }

    private void continueGame() {
        score = GameState.savedScore;
        moves = GameState.savedMoves;
        GameState.load(board);
    }

    private void saveGame() {
        GameState.save(board, score, moves);
    }

    private void createBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = random.nextInt(candyColors.length);
            }
        }

        ArrayList<Point> matches = findMatches();

        while (!matches.isEmpty()) {
            for (Point p : matches) {
                board[p.y][p.x] = random.nextInt(candyColors.length);
            }
            matches = findMatches();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawHeader(g);
        drawBoard(g);
        drawCandies(g);
        drawGameStatus(g);
    }

    private void drawHeader(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(new Color(220, 60, 120));
        g.drawString("Candy Crush", 30, 40);

        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.DARK_GRAY);
        g.drawString("Score: " + score, 350, 35);
        g.drawString("Moves: " + moves, 350, 60);
    }

    private void drawBoard(Graphics g) {
        g.setColor(new Color(230, 210, 240));
        g.fillRoundRect(BOARD_X - 10, BOARD_Y - 10, SIZE * CELL_SIZE + 20, SIZE * CELL_SIZE + 20, 20, 20);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int x = BOARD_X + col * CELL_SIZE;
                int y = BOARD_Y + row * CELL_SIZE;

                g.setColor(Color.WHITE);
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);

                g.setColor(new Color(210, 190, 220));
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }

        if (selected != null) {
            g.setColor(Color.BLACK);
            g.drawRect(
                    BOARD_X + selected.x * CELL_SIZE,
                    BOARD_Y + selected.y * CELL_SIZE,
                    CELL_SIZE,
                    CELL_SIZE
            );

            g.drawRect(
                    BOARD_X + selected.x * CELL_SIZE + 1,
                    BOARD_Y + selected.y * CELL_SIZE + 1,
                    CELL_SIZE - 2,
                    CELL_SIZE - 2
            );
        }
    }

    private void drawCandies(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {

                if (board[row][col] == -1) {
                    continue;
                }

                boolean isRemoving = false;

                for (Point p : removingCandies) {
                    if (p.x == col && p.y == row) {
                        isRemoving = true;
                        break;
                    }
                }

                int x = BOARD_X + col * CELL_SIZE;
                int y = BOARD_Y + row * CELL_SIZE;

                if (isRemoving) {
                    drawCandy(g2, x, y, board[row][col], animationScale);
                } else {
                    drawCandy(g2, x, y, board[row][col], 1.0f);
                }
            }
        }
    }

    private void drawCandy(Graphics2D g2, int x, int y, int type, float scale) {
        Graphics2D temp = (Graphics2D) g2.create();

        float alpha = scale;
        if (alpha < 0.1f) {
            alpha = 0.1f;
        }

        temp.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int candySize = (int) (CELL_SIZE * 0.72 * scale);
        int offset = (CELL_SIZE - candySize) / 2;

        temp.setColor(candyColors[type]);
        temp.fillOval(x + offset, y + offset, candySize, candySize);

        temp.setColor(Color.WHITE);
        temp.fillOval(x + offset + candySize / 5, y + offset + candySize / 5, candySize / 4, candySize / 4);

        temp.setColor(Color.DARK_GRAY);
        temp.drawOval(x + offset, y + offset, candySize, candySize);

        temp.dispose();
    }

    private void drawGameStatus(Graphics g) {
        if (moves <= 0) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 42));
            g.drawString("GAME OVER", 170, 300);

            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Final Score: " + score, 210, 345);
        }
    }

    private boolean isInsideBoard(int mouseX, int mouseY) {
        return mouseX >= BOARD_X
                && mouseX < BOARD_X + SIZE * CELL_SIZE
                && mouseY >= BOARD_Y
                && mouseY < BOARD_Y + SIZE * CELL_SIZE;
    }

    private boolean isNeighbor(Point a, Point b) {
        int dx = Math.abs(a.x - b.x);
        int dy = Math.abs(a.y - b.y);

        return dx + dy == 1;
    }

    private void swap(Point a, Point b) {
        int temp = board[a.y][a.x];
        board[a.y][a.x] = board[b.y][b.x];
        board[b.y][b.x] = temp;
    }

    private ArrayList<Point> findMatches() {
        ArrayList<Point> matches = new ArrayList<>();

        // Check horizontal matches
        for (int row = 0; row < SIZE; row++) {
            int count = 1;

            for (int col = 1; col < SIZE; col++) {
                if (board[row][col] != -1 && board[row][col] == board[row][col - 1]) {
                    count++;
                } else {
                    if (count >= 3) {
                        for (int k = 0; k < count; k++) {
                            addMatch(matches, col - 1 - k, row);
                        }
                    }
                    count = 1;
                }
            }

            if (count >= 3) {
                for (int k = 0; k < count; k++) {
                    addMatch(matches, SIZE - 1 - k, row);
                }
            }
        }

        // Check vertical matches
        for (int col = 0; col < SIZE; col++) {
            int count = 1;

            for (int row = 1; row < SIZE; row++) {
                if (board[row][col] != -1 && board[row][col] == board[row - 1][col]) {
                    count++;
                } else {
                    if (count >= 3) {
                        for (int k = 0; k < count; k++) {
                            addMatch(matches, col, row - 1 - k);
                        }
                    }
                    count = 1;
                }
            }

            if (count >= 3) {
                for (int k = 0; k < count; k++) {
                    addMatch(matches, col, SIZE - 1 - k);
                }
            }
        }

        return matches;
    }

    private void addMatch(ArrayList<Point> matches, int x, int y) {
        for (Point p : matches) {
            if (p.x == x && p.y == y) {
                return;
            }
        }

        matches.add(new Point(x, y));
    }

    private void removeMatches(ArrayList<Point> matches) {
        for (Point p : matches) {
            board[p.y][p.x] = -1;
        }

        score += matches.size() * 10;
    }

    private void applyGravity() {
        for (int col = 0; col < SIZE; col++) {
            int emptyRow = SIZE - 1;

            for (int row = SIZE - 1; row >= 0; row--) {
                if (board[row][col] != -1) {
                    board[emptyRow][col] = board[row][col];

                    if (emptyRow != row) {
                        board[row][col] = -1;
                    }

                    emptyRow--;
                }
            }
        }
    }

    private void refillBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == -1) {
                    board[row][col] = random.nextInt(candyColors.length);
                }
            }
        }
    }

    private void animateRemove(ArrayList<Point> matches) {
        isAnimating = true;
        removingCandies.clear();
        removingCandies.addAll(matches);
        animationScale = 1.0f;

        Timer timer = new Timer(40, null);

        timer.addActionListener(e -> {
            animationScale -= 0.12f;

            if (animationScale <= 0.1f) {
                timer.stop();

                removeMatches(matches);
                removingCandies.clear();

                applyGravity();
                refillBoard();

                ArrayList<Point> newMatches = findMatches();

                if (!newMatches.isEmpty()) {
                    repaint();
                    animateRemove(newMatches);
                } else {
                    isAnimating = false;
                    animationScale = 1.0f;
                    saveGame();
                    repaint();
                }
            }

            repaint();
        });

        timer.start();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (isAnimating || moves <= 0) {
            return;
        }

        int mouseX = e.getX();
        int mouseY = e.getY();

        if (!isInsideBoard(mouseX, mouseY)) {
            return;
        }

        int col = (mouseX - BOARD_X) / CELL_SIZE;
        int row = (mouseY - BOARD_Y) / CELL_SIZE;

        Point clicked = new Point(col, row);

        if (selected == null) {
            selected = clicked;
            repaint();
            return;
        }

        if (selected.x == clicked.x && selected.y == clicked.y) {
            selected = null;
            repaint();
            return;
        }

        if (!isNeighbor(selected, clicked)) {
            selected = clicked;
            repaint();
            return;
        }

        swap(selected, clicked);

        ArrayList<Point> matches = findMatches();

        if (matches.isEmpty()) {
            swap(selected, clicked);
            selected = null;
            repaint();
        } else {
            moves--;
            selected = null;
            animateRemove(matches);
        }

        saveGame();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
