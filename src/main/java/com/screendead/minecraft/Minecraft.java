package com.screendead.minecraft;

import com.screendead.minecraft.graphics.*;
import org.lwjgl.*;

import static org.lwjgl.glfw.GLFW.*;

public class Minecraft {
    private Window window;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        // Create the window
        window = new Window("Minecraft", 800, 800);

        // Start the game loop
        loop();

        window.destroy();
    }

    private void loop() {
        window.initImage("C:/Users/admin/Documents/IntelliJ IDEA Projects/Minecraft/res/img/heart.png");

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window.getHandle()) ) {
            window.render();

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        // Create an instance and start the Game Loop
        new Minecraft().run();
    }
}
