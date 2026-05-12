public class GameState {

    public static boolean hasSavedGame = false;

    public static int savedScore = 0;
    public static int savedMoves = 20;

    public static int[][] savedBoard = new int[8][8];

    public static void save(int[][] board, int score, int moves) {
        savedScore = score;
        savedMoves = moves;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                savedBoard[row][col] = board[row][col];
            }
        }

        hasSavedGame = true;
    }

    public static void load(int[][] board) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = savedBoard[row][col];
            }
        }
    }
}