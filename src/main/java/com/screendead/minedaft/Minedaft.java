package com.screendead.minedaft;

import com.screendead.minedaft.graphics.Window;

import javax.swing.*;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class Minedaft {
    private Window window;
    private float UPS = 60.0f, FPS = 60.0f;

    /**
     * Begin the game
     */
    private void run() {
        // Create the window
        window = new Window("Minedaft", 1080, 720, true, true);

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
                window.update(totalTicks);
                ticks++;
                totalTicks++;
                deltaU--;
            }

            if (deltaF >= 1) {
                window.render();
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                System.out.println(String.format("UPS: %s, FPS: %s", ticks, frames));
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
        if (!args[0].equals("160575")) {
            JOptionPane.showMessageDialog(null, "You may not run this program without using the Launcher", "Unauthorised", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Create an instance and start the Game Loop
        new Minedaft().run();
    }

    public static String getResource(String name) {
        String out = "./assets/resources/" + name;
        System.out.println("Loading " + out + " ...");
        return out;
    }
}
