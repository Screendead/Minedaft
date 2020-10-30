package com.screendead.minedaft;

import com.screendead.minedaft.graphics.Window;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class Minedaft {
    private Window window;
    private static final float UPS = 40.0f, FPS = 60.0f;

    /**
     * Begin the game
     */
    private void run() {
        // Create the window
        window = new Window("Minedaft", 2560/2, 1440/2, false, true);

        // Start the game loop
        loop();

        // Destroy the window after exit
        window.destroy();
    }

    /**
     * The main game loop
     */
    private void loop() {
        long initialTime = System.nanoTime();
        final float timeU = 1000000000.0f / UPS;
        final float timeF = 1000000000.0f / FPS;
        float deltaU = 0, deltaF = 0;
        int frames = 0, ticks = 0, totalTicks = 0;
        long timer = System.currentTimeMillis();

        while (!glfwWindowShouldClose(window.getHandle())) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime - initialTime) / timeU;
            deltaF += (currentTime - initialTime) / timeF;
            initialTime = currentTime;

            if (deltaU >= 1) {
                glfwPollEvents();
                ticks++;
                totalTicks++;
                window.update(totalTicks);
                deltaU--;
            }

            if (deltaF >= 1) {
                window.render();
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                System.out.printf("UPS: %s, FPS: %s%n", ticks, frames);
                frames = 0;
                ticks = 0;
                timer += 1000;
            }
        }
    }

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            JOptionPane.showMessageDialog(null, "Please open this program through the included Launcher", "Unauthorised", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        if (!sha256(args[0]).equals("f9407f3af467e2f3889c87958019e0fa0a4ca058b24227cf34571c35d20d7e58")) {
            JOptionPane.showMessageDialog(null, "You may not run this program without using the Launcher", "Unauthorised", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Create an instance and start the Game Loop
        new Minedaft().run();
    }

    public static String getResource(String name) {
        String out = "./resources/" + name;
        System.out.println("Loading " + out + " ...");
        return out;
    }

    private static String sha256(String in) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert digest != null;
        byte[] hash = digest.digest(in.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
