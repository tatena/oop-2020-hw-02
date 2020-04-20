import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class JTetrisBrain extends JTetris{

    private static final int BRAIN = 5;
    private JCheckBox brainMode;
    private Brain brain;
    private Brain.Move bestMove;
    private JSlider adversary;
    private JLabel adversaryLabel;

    /**
     * Creates a new JTetris where each tetris square
     * is drawn with the given number of pixels.
     *
     * @param pixels
     */
    JTetrisBrain(int pixels) {
        super(pixels);
        bestMove = new Brain.Move();
        brain = new DefaultBrain();
        timer = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(DOWN);
            }
        });
    }

    @Override
    public JComponent createControlPanel() {
        JPanel panel =  (JPanel) super.createControlPanel();

        panel.add(new JLabel("Brain:"));
        brainMode = new JCheckBox("Brain active");
        panel.add(brainMode);

        JPanel little = new JPanel();
        little.setLayout(new GridLayout(3, 1));

        JLabel textAd = new JLabel("Adversary:");
        textAd.setHorizontalAlignment(SwingConstants.CENTER);
        textAd.setVerticalAlignment(SwingConstants.BOTTOM);
        little.add(textAd);

        adversary = new JSlider(0, 100, 0); // min, max, current
        adversary.setPreferredSize(new Dimension(100,15));
        little.add(adversary);

        adversaryLabel = new JLabel("ok");
        adversaryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        adversaryLabel.setVerticalAlignment(SwingConstants.TOP);
        little.add(adversaryLabel);

        panel.add(little);

        return panel;
    }

    @Override
    public void tick(int verb) {
        if (verb == DOWN && brainMode.isSelected()) {
            brainMove();
        }
        super.tick(verb);
    }

    @Override
    public void addNewPiece() {
        super.addNewPiece();
        board.undo();
        bestMove = brain.bestMove(board, currentPiece, getHeight() - TOP_SPACE , null);
    }

    private void brainMove() {
        if (bestMove == null) { return; }

        //rotate
        if (!currentPiece.equals(bestMove.piece)) { tick(JTetris.ROTATE); }

        //left or right movement
        if (bestMove.x < currentX) {
           tick(JTetris.LEFT);
        } else if (bestMove.x > currentX) {
           tick(JTetris.RIGHT);
        }
    }


    @Override
    public Piece pickNextPiece() {
        int rand = random.nextInt(100);
        if (adversary.getValue() > rand) {
            adversaryLabel.setText("*ok*");
            return getWordsPiece();
        } else {
            adversaryLabel.setText("ok");
            return super.pickNextPiece();
        }
    }

    private Piece getWordsPiece() {
        double score = Double.MIN_VALUE;
        Piece res = null;
        Brain.Move curr;
        for (int i = 0; i < pieces.length; i++) {
            curr = brain.bestMove(board, pieces[i], getHeight() - TOP_SPACE, null);
            if (curr.score > score) {
                res = pieces[i];
                score = curr.score;
            }
        }
        return res;
    }

    /**
     Creates a frame with a JTetris.
     */
    public static void main(String[] args) {
        // Set GUI Look And Feel Boilerplate.
        // Do this incantation at the start of main() to tell Swing
        // to use the GUI LookAndFeel of the native platform. It's ok
        // to ignore the exception.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        JTetrisBrain tetris = new JTetrisBrain(16);
        JFrame frame = JTetrisBrain.createFrame(tetris);
        frame.setVisible(true);
    }

}
