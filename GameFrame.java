import javax.swing.JFrame;

public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("Candy Crush Game");
        setSize(600, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        showMenu();

        setVisible(true);
    }

    public void showMenu() {
        setContentPane(new MenuPanel(this));
        revalidate();
        repaint();
    }

    public void startNewGame() {
        GamePanel panel = new GamePanel(this, true);
        setContentPane(panel);
        revalidate();
        repaint();
        panel.requestFocusInWindow();
    }

    public void continueGame() {
        GamePanel panel = new GamePanel(this, false);
        setContentPane(panel);
        revalidate();
        repaint();
        panel.requestFocusInWindow();
    }
}
