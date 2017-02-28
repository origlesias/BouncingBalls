package bouncballs_nb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerServer implements Runnable {
    private static final int PORT = 9090;
    private static HashSet<String> names = new HashSet<>();
    private static HashSet<PrintWriter> writers = new HashSet<>();

    static Player player;

    public ControllerServer(Player player) {
        ControllerServer.player = player;
    }

    @Override
    public void run() {
        ServerSocket listener;
        try {
            listener = new ServerSocket(PORT);
            System.out.println("Servidor a la escucha @"
                    + InetAddress.getLocalHost().getHostAddress() + ":"
                    + PORT);
            try {
                while (true) {
                    new Handler(listener.accept()).start();
                }
            } finally {
                listener.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Handler extends Thread {

        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    // Envía el mensaje de Servidor a la escucha
                    out.println("CONNECT");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            System.out.println("Conexión realizada con éxito por " + name
                                    + socket.getInetAddress());
                            break;
                        }
                    }
                }

                // Envía el mensaje de conexión aceptada
                out.println("CONN_OK");
                writers.add(out);

                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ControllerServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if(input.contains("MOVE")){
                        String[] split= input.split(",");
                        player.move(Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                    }
                    switch (input) {
                        case "L":
                            player.moveLeft();
                            break;
                        case "R":
                            player.moveRight();
                            break;
                        case "UL":
                            player.moveLUp();
                            break;
                        case "U":
                            player.moveUp();
                            break;
                        case "UR":
                            player.moveRUp();
                            break;
                        case "BL":
                            player.moveLBottom();
                            break;
                        case "B":
                            player.moveBottom();
                            break;
                        case "BR":
                            player.moveRBottom();
                            break;
                        case "BYE":
                            System.exit(0);
                            break;
                        case "PAUSE":
                            BouncingBalls.PAUSED = !BouncingBalls.PAUSED;
                            break;
                        case "TURBO":
                            BouncingBalls.TURBO = !BouncingBalls.TURBO;
                            break;
                        case "DEBUG":
                            BouncingBalls.DEBUGGING = !BouncingBalls.DEBUGGING;
                            break;
                        default:
                            // Nothing
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}