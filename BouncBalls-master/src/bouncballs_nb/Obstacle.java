package bouncballs_nb;

import java.awt.*;

public class Obstacle {

    int posX;
    int posY;

    int width;
    int height;

    public Obstacle(int posX, int posY, int width, int height) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }
    
    public Rectangle getLimits() {
        return new Rectangle(posX, posY, width, height);
    }

    public void draw(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fill3DRect(posX, posY, width, height, true);
    }
}
