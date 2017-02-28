package bouncballs_nb;


import com.sun.javafx.geom.Vec2d;
import java.util.ArrayList;
import javafx.scene.shape.Circle;

public class CollisionPhysics {

    private static final double angle90degrees = Math.PI / 2;
    
    public static void checkBall2BlackHoleCollision(Ball b, ArrayList<BlackHole> blackHoles) {
        for (BlackHole blackHole : blackHoles) {
            if (blackHole.inside(b) || blackHole.ball == b) {
                blackHole.peaje(b);
            }
        }
    }

    public static void checkBall2WallCollision(Ball b, Space space) {

        // Comprueba en qué lado de la ventana ha colisionado, y dependiendo de ello calcula la nueva velocidad
        // X o Y como el valor absoluto (en positivo o negativo, dependiendo del lado de colisión) de la propia velocidad.
        if (b.posX + b.radius >= space.WIDTH) {
            b.speedX = -Math.abs(b.speedX);
        }
        if (b.posX - b.radius <= 0) {
            b.speedX = Math.abs(b.speedX);
        }

        if (b.posY + b.radius >= space.HEIGHT) {
            b.speedY = -Math.abs(b.speedY);
        }
        if (b.posY - b.radius <= 0) {
            b.speedY = Math.abs(b.speedY);
        }
        
        if(b.posY+b.radius>space.HEIGHT) b.posY=space.HEIGHT-b.radius;
        if(b.posX+b.radius>space.WIDTH) b.posX=space.WIDTH-b.radius;
    }

    public static void checkBall2ObstacleCollision(Ball b, ArrayList<Obstacle> obstacles) {
        Circle c = new Circle(b.posX, b.posY, b.radius);
        for (Obstacle obs : obstacles) {
            if(c.intersects(obs.posX, obs.posY, obs.width, obs.height)) {
                if (b.posX <= obs.posX || b.posX >= obs.width + obs.posX) {
                    b.bounceX();
                }
                if (b.posY <= obs.posY || b.posY >= obs.height + obs.posY) {
                    b.bounceY();
                }
            }
        }
    }

    public static synchronized void checkBall2BallCollision(Ball ba, ArrayList<Ball> balls) {
        // Variables usadas en las comrobaciones
        double r, d_mod;
        Vec2d d;

        for (Ball ball : balls) { // Comprueba respecto a todas las bolas del espacio
            if (ball != ba) {     // exceptuando la propia bola

                r = ba.radius + ball.radius; // Suma de los radios de las bolas, para compro
                d = new Vec2d(ball.posX - ba.posX, ball.posY - ba.posY); // Vector de distancia entre centros
                d_mod = Math.hypot(d.x, d.y); // Modulo del vector de distancia

                if (d_mod <= r) { // Si el modulo del vector de distancia es menor a la suma de los radios
                    calcBounce(ba, ball);
                }
            }
        }
    }

    // Primer método, ya no se usa, a veces fallaba
    public static Vec2d calculateBounce(Ball b1, Ball b2) {
        Vec2d d = new Vec2d(b2.posX - b1.posX, b2.posY - b1.posY); // Vector de distancia entre centros
        double d_mod = Math.hypot(d.x, d.y); // Modulo del vector de distancia
        Vec2d v_ini = new Vec2d(b1.speedX, b1.speedY); // Vector de velocidad inicial
        Vec2d ud = new Vec2d(d.x / d_mod, d.y / d_mod); // Vector unitario de distancia
        double v_mod = Math.hypot(v_ini.x, v_ini.y); // Modulo del vector de velocidad inicial
        Vec2d uv_ini = new Vec2d(v_ini.x / v_mod, v_ini.y / v_mod); // Vector unitario de la velocidad inicial
        double delta = Math.acos(ud.x); // Angulo del vector de distancia respecto al eje X
        double beta = Math.acos(uv_ini.x); // Angulo del vector de velocidad incial respecto al eje X
        double bounce_angle = beta + (2 * (angle90degrees - (beta - delta))); // Angulo de rebote
        Vec2d uv_fin = new Vec2d(Math.cos(bounce_angle), Math.sin(bounce_angle)); // Vector unitario de velocidad final
        Vec2d v_fin = new Vec2d(uv_fin.x * v_mod, uv_fin.y * v_mod); // Vector de velocidad final
        return v_fin;
    }
    
    // Nuevo sistema de colisiones
    public static void calcBounce(Ball b1, Ball b2) {
        // Ángulo de colision entre las bolas
        double collAngle = Math.atan2((b2.posY - b1.posY), (b2.posX - b1.posX));
        // Velocidad de la bola 1
        Vec2d v_b1 = new Vec2d(b1.speedX, b1.speedY);
        double mod_v_b1 = Math.hypot(v_b1.x, v_b1.y);
        // Velocidad de la bola 2
        Vec2d v_b2 = new Vec2d(b2.speedX, b2.speedY);
        double mod_v_b2 = Math.hypot(v_b2.x, v_b2.y);
        // Calcula direcciones
        double d1 = Math.atan2(v_b1.y, v_b1.x);
        double d2 = Math.atan2(v_b2.y, v_b2.x);
        // Calcula las nuevas velocidades relativas
        double new_xSpeed_b1 = mod_v_b1 * Math.cos(d1 - collAngle);
        double new_ySpeed_b1 = mod_v_b1 * Math.sin(d1 - collAngle);
        double new_xSpeed_b2 = mod_v_b2 * Math.cos(d2 - collAngle);
        double new_ySpeed_b2 = mod_v_b2 * Math.sin(d2 - collAngle);
        // Calcula las nuevas velocidades finales
        double fin_xSpeed_b1 = ((b1.mass - b2.mass) * new_xSpeed_b1 + (2 * b2.mass) * new_xSpeed_b2) / (b1.mass + b2.mass);
        double fin_xSpeed_b2 = ((2 * b1.mass) * new_xSpeed_b1 + (b2.mass - b1.mass) * new_xSpeed_b2) / (b1.mass + b2.mass);
        double fin_ySpeed_b1 = new_ySpeed_b1;
        double fin_ySpeed_b2 = new_ySpeed_b2;
        // Aplica las velocidades finales al ángulo de posicion
        b1.speedX = (float) (Math.cos(collAngle) * fin_xSpeed_b1 - Math.sin(collAngle) * fin_ySpeed_b1);
        b1.speedY = (float) (Math.sin(collAngle) * fin_xSpeed_b1 + Math.cos(collAngle) * fin_ySpeed_b1);
        b2.speedX = (float) (Math.cos(collAngle) * fin_xSpeed_b2 - Math.sin(collAngle) * fin_ySpeed_b1);
        b2.speedY = (float) (Math.sin(collAngle) * fin_xSpeed_b2 + Math.cos(collAngle) * fin_ySpeed_b2);
        // Pone las posiciones de las bolas como vectores para facilitar el cálculo
        Vec2d pos_b1 = new Vec2d(b1.posX, b1.posY);
        Vec2d pos_b2 = new Vec2d(b2.posX, b2.posY);
        // Calcula las diferencias entre las posiciones de las bolas
        Vec2d posDiff = new Vec2d(pos_b1.x - pos_b2.x, pos_b1.y - pos_b2.y);
        double mod_posDiff = Math.hypot(posDiff.x, posDiff.y);
        double scale = (((b1.radius + b2.radius) - mod_posDiff) / mod_posDiff);
        Vec2d mtd = new Vec2d(posDiff.x * scale, posDiff.y * scale);
        // Calcula las inversas de las masas de las bolas
        double inv_mass_b1 = 1 / b1.mass;
        double inv_mass_b2 = 1 / b2.mass;
        // Calcula las nuevas posiciones para evitar bugs de solapamiento de las bolas
        pos_b1 = new Vec2d(pos_b1.x + (mtd.x * (inv_mass_b1 / (inv_mass_b1 + inv_mass_b2))), 
                            pos_b1.y + (mtd.y * (inv_mass_b1 / (inv_mass_b1 + inv_mass_b2))));
        pos_b2 = new Vec2d(pos_b2.x - (mtd.x * (inv_mass_b2 / (inv_mass_b1 + inv_mass_b2))), 
                            pos_b2.y - (mtd.y * (inv_mass_b2 / (inv_mass_b1 + inv_mass_b2))));
        // Establece las nuevas posiciones
        b1.posX = (float) pos_b1.x;
        b1.posY = (float) pos_b1.y;
        b2.posX = (float) pos_b2.x;
        b2.posY = (float) pos_b2.y;
    }
}
