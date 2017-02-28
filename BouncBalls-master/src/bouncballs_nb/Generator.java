package bouncballs_nb;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Generator implements Runnable{

    private Space space;
    private Color[] colors = {Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.YELLOW, Color.ORANGE,
            Color.GREEN, Color.LIGHT_GRAY, Color.RED, Color.MAGENTA, Color.PINK, Color.WHITE};

    public Generator(Space space) {
        this.space = space;
    }

    public Ball randomBall() {
        float posX = ThreadLocalRandom.current().nextInt(23, space.WIDTH - 23);
        float posY = ThreadLocalRandom.current().nextInt(23, space.HEIGHT - 23);
        float radius = ThreadLocalRandom.current().nextInt(4, 25);
        float mass = radius;
        float speed = ThreadLocalRandom.current().nextInt(2, 8);
        float angle = ThreadLocalRandom.current().nextInt(0, 359);
        Color color = colors[ThreadLocalRandom.current().nextInt(0, colors.length - 1)];
        return new Ball(space, posX, posY, radius, mass, speed, angle, color);
    }

    @Override
    public void run() {

    }
}
