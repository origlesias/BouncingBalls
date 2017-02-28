

package bouncballs_nb;

// @author: Oriol Iglesias

import com.sun.javafx.geom.Vec2d;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;


public class Wormhole implements Runnable{

    float posX;
    float posY;
    float exitX;
    float exitY;
    int cont=0;
    
    private Space space;

    public Wormhole(float posX, float posY, float exitX, float exitY, Space space) {
        this.posX = posX;
        this.posY = posY;
        this.exitX = exitX;
        this.exitY = exitY;
        this.space = space;
    }
    
    public void draw(Graphics g){
        Graphics2D gg= (Graphics2D) g;
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gg.setColor(Color.BLUE);
        gg.drawOval((int) posX, (int) posY-40, 50, 80);
        gg.setColor(Color.ORANGE);
        gg.drawOval((int) exitX, (int) exitY-40, 50, 80);
    }
    
    private synchronized void gravity(Ball b){
        Vec2d d;
        double d_mod;
        
          d = new Vec2d(posX - b.posX, posY - b.posY);
          d_mod = Math.hypot(d.x, d.y);
          if(d_mod<250&&!b.waiting){
              b.speedX+=d.x/(20*d_mod);
              b.speedY+=d.y/(20*d_mod);
          }
          if(d_mod<30){
              b.posX= exitX;
              b.posY= exitY;
              cont++;
              if(cont%5==0){
                reposition();
            }
          }
    }
    
    private void reposition(){
        Random r= new Random();
        posX= r.nextInt(800)+250;
        posY= r.nextInt(500)+100;
        do{
        exitX= r.nextInt(800)+250;
        }while(Math.abs(posX-exitX)<100);
        do{
        exitY= r.nextInt(500)+100;
        }while(Math.abs(posY-exitY)<100);
    }
    
    @Override
    public void run() {
        while (true) {
            for(Ball b:space.getBalls()){
            this.gravity(b);
            }
            do {
                try {
                    
                        Thread.sleep(6);
                } catch (InterruptedException ex) {
                }
            } while (BouncingBalls.PAUSED);
        }
        }

}
