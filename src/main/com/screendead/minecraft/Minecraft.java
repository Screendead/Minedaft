package com.screendead.minecraft;

import com.screendead.minecraft.graphics.Window;
import org.lwjgl.Version;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

public class Minecraft {
    private Window window;
    private float UPS = 60.0f, FPS = 60.0f;

    /**
     * Begin the game
     */
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        // Create the window
        window = new Window("Minecraft", 1080, 720, true, true);

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
        // Create an instance and start the Game Loop
        new Minecraft().run();
    }
}
