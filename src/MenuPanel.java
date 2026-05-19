import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {

    private GameFrame frame;

    public MenuPanel(GameFrame frame) {
        this.frame = frame;

        setLayout(null);
        setBackground(new Color(255, 230, 240));

        JLabel title = new JLabel("Candy Crush", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setForeground(new Color(220, 60, 120));
        title.setBounds(80, 80, 440, 70);
        add(title);

        JLabel subTitle = new JLabel("Java Swing Project", SwingConstants.CENTER);
        subTitle.setFont(new Font("Arial", Font.PLAIN, 20));
        subTitle.setForeground(Color.DARK_GRAY);
        subTitle.setBounds(100, 145, 400, 40);
        add(subTitle);

        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 24));
        newGameButton.setBounds(190, 240, 220, 55);
        add(newGameButton);

        JButton continueButton = new JButton("Continue");
        continueButton.setFont(new Font("Arial", Font.BOLD, 24));
        continueButton.setBounds(190, 320, 220, 55);
        add(continueButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 24));
        exitButton.setBounds(190, 400, 220, 55);
        add(exitButton);

        newGameButton.addActionListener(e -> {
            frame.startNewGame();
        });

        continueButton.addActionListener(e -> {
            if (GameState.hasSavedGame) {
                frame.continueGame();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No saved game found. Please start a New Game first."
                );
            }
        });

        exitButton.addActionListener(e -> {
            System.exit(0);
        });
    }
}
