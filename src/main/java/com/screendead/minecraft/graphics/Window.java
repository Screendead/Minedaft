package com.screendead.minecraft.graphics;

import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private long handle;
    public Renderer renderer;

    public Window(String title, int width, int height) {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create a handle for the window
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (handle == NULL)
            throw new RuntimeException("Failed to create the GLFW window.");

        setIcon("C:/Users/admin/Documents/IntelliJ IDEA Projects/Minecraft/res/img/heart.png");

        renderer = new Renderer();

        // Make the OpenGL context current
        glfwMakeContextCurrent(handle);
        renderer.init();
        // Enable v-sync
        glfwSwapInterval(0);

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(handle, (handle, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(handle, true); // We will detect this in the rendering loop
        });

        // Setup a resizing callback. Make sure the window behaves the way it should when resizing
        glfwSetWindowSizeCallback(handle, (handle, w, h) -> {
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            int minSize = 200;
            glfwSetWindowSizeLimits(handle, minSize, minSize, Math.max(Math.max(vidmode.width(), minSize), h), Math.max(Math.min(w, vidmode.height()), minSize));
            this.autoViewport();
            this.centre();
            this.render();
        });

        // Centre and set viewport for the window
        this.autoViewport();
        this.centre();

        // Make the window visible
        glfwShowWindow(handle);
    }

    /**
     * Use the renderer to draw to the window
     */
    public void render() {
        renderer.render();

        // Draw buffer to the screen
        glfwSwapBuffers(handle);
    }

    /**
     * Frees callbacks and destroys the window
     */
    public void destroy() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    /**
     * Reset the viewport based on window size
     */
    private void autoViewport() {
        Vector2i size = this.getSize();

        renderer.setViewport(size.x, size.y);
    }

    /**
     * Centre the window
     */
    private void centre() {
        Vector2i size = this.getSize();

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidmode == null) throw new RuntimeException("Failed to get the vidmode.");

        // Center the window
        glfwSetWindowPos(
                handle,
                (vidmode.width() - size.x) / 2,
                (vidmode.height() - size.y) / 2
        );
    }

    /**
     * Set the icon of the window
     * @param source The image to use as icon
     */
    public void setIcon(String source) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = BufferUtils.createIntBuffer(1),
                    h = BufferUtils.createIntBuffer(1),
                    channels = BufferUtils.createIntBuffer(1);

            ByteBuffer img = STBImage.stbi_load(source, w, h, channels, 4);
            if (img == null) throw new RuntimeException("Icon failed to load.");

            glfwSetWindowIcon(handle, GLFWImage.create(1).put(0, GLFWImage.create().set(w.get(), h.get(), img)));
        }
    }

    /**
     * Get the size of the window in pixels
     * @return the size of the window
     */
    private Vector2i getSize() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(handle, pWidth, pHeight);
            return new Vector2i(pWidth.get(), pHeight.get());
        }
    }

    /**
     * @return handle The handle of the window
     */
    public long getHandle() { return handle; }
}
