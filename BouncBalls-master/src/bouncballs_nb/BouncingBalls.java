package bouncballs_nb;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;

public class BouncingBalls extends JFrame {

    static boolean DEBUGGING = false;
    static boolean PAUSED = false;
    static boolean TURBO = false;
    private Space space;

    public BouncingBalls() throws IOException {
        space = new Space(1300, 700);
        createFrame();
        new Thread(space).start();
    }

    private void createFrame() {
        Container panel;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setResizable(false);
        panel = getContentPane();
        panel.add(space);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            BouncingBalls bb = new BouncingBalls();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
