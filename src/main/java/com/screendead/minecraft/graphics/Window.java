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
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private long handle;
    private boolean fullscreen = false, visible = false;
    private int initialWidth, initialHeight;
    private int width, height;
    public Renderer renderer;
    private long monitor;
    private GLFWVidMode v;

    public Window(String title, int width, int height, boolean isFullscreen) {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        monitor = glfwGetPrimaryMonitor();
        v = glfwGetVideoMode(monitor);

        initialWidth = width;
        initialHeight = height;

        if (fullscreen) {
            this.width = v.width();
            this.height = v.height();
        } else {
            this.width = width;
            this.height = height;
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_SAMPLES, 4096); // Enable MSAA
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create a handle for the window
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (handle == NULL)
            throw new RuntimeException("Failed to create the GLFW window.");

        // Set the icon of the window
        setIcon("C:/Users/admin/Documents/IntelliJ IDEA Projects/Minecraft/res/img/heart.png");

        // Set the window size limits
        int minSize = 200;
        glfwSetWindowSizeLimits(handle, minSize, minSize, v.width(), v.height());
        this.centre();

        renderer = new Renderer();

        // Make the OpenGL context current
        glfwMakeContextCurrent(handle);
        renderer.init();

        this.autoViewport();

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(handle, (handle, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(handle, true); // We will detect this in the rendering loop
            else if (key == GLFW_KEY_F11 && action == GLFW_RELEASE)
                this.toggleFullscreen();
        });

        // Setup a resizing callback. Make sure the window behaves the way it should when resizing
        glfwSetWindowSizeCallback(handle, (handle, w, h) -> {
            this.width = w;
            this.height = h;
            this.autoViewport();
            this.render();
        });

        if (isFullscreen) toggleFullscreen();

        // Make the window visible
        this.toggleVisibility();
    }

    public void toggleFullscreen() {
        toggleVisibility();

        fullscreen = !fullscreen;

        if (fullscreen) {
            width = v.width();
            height = v.height();

            // Toggle the window to fullscreen
            glfwSetWindowMonitor(handle, monitor, 0, 0, width, height, v.refreshRate());

            // Enable v-sync
            glfwSwapInterval(1);
        } else {
            width = initialWidth;
            height = initialHeight;

            // Toggle the window to windowed mode
            glfwSetWindowMonitor(handle, NULL, 0, 0, width, height, 0);

            this.centre();
        }

        this.autoViewport();

        toggleVisibility();
    }

    public void toggleVisibility() {
        visible = !visible;

        if (visible) {
            glfwShowWindow(handle);
        } else {
            glfwHideWindow(handle);
        }

        glfwFocusWindow(handle);
    }

    /**
     * Update the game
     */
    public void update(int start) {
        renderer.setTransform(0, 0, 0,
                0, (float) start, 0,
                1.0f, 1.0f, 1.0f);
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
     * @return the size of the window in pixels
     */
    private Vector2i getSize() {
        return new Vector2i(this.width, this.height);
    }

    /**
     * @return handle The handle of the window
     */
    public long getHandle() { return handle; }
}
