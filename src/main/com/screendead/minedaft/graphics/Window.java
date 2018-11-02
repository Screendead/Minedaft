package com.screendead.minedaft.graphics;

import com.screendead.minedaft.Input;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private long handle;
    private Input input;
    private boolean fullscreen = false, visible = false;
    private int initialWidth, initialHeight;
    private int width, height;
    public Renderer renderer;
    private long monitor;
    private GLFWVidMode v;
    private int vsync;
    private Camera camera;

    public Window(String title, int width, int height, boolean isFullscreen, boolean vsyncEnabled) {
        vsync = (vsyncEnabled) ? 1 : 0;

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
//        glfwWindowHint(GLFW_SAMPLES, 4); // Enable MSAA
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // The window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // The window will be resizable
        glfwWindowHint(GLFW_AUTO_ICONIFY, GLFW_FALSE); // The window will be alt-tabbable without iconifying

        // Create a handle for the window
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (handle == NULL)
            throw new RuntimeException("Failed to create the GLFW window.");

        glfwSetInputMode(this.handle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN); // The cursor will be hidden

        // Set the icon of the window
        setIcon("C:/Users/admin/Documents/IntelliJ IDEA Projects/Minedaft/res/img/heart.png");

        // Set the window size limits
        int minSize = 200;
        glfwSetWindowSizeLimits(handle, minSize, minSize, v.width(), v.height());
        this.centre();

        renderer = new Renderer();

        // Make the OpenGL context current
        glfwMakeContextCurrent(handle);
        renderer.init();

        camera = new Camera(new Vector3f(0, 0, 3));

        this.autoViewport();

        glfwSwapInterval(vsync);

        if (isFullscreen) toggleFullscreen();

        input = new Input(this);

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
        } else {
            width = initialWidth;
            height = initialHeight;

            // Toggle the window to windowed mode
            glfwSetWindowMonitor(handle, NULL, 0, 0, width, height, 0);

            this.centre();
        }

        this.autoViewport();

        glfwSwapInterval(vsync);

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
    public void update(int ticks) {
        if (input.keys[GLFW_KEY_ESCAPE])
            glfwSetWindowShouldClose(handle, true);
        else if (input.keys[GLFW_KEY_F11])
            this.toggleFullscreen();

        if (key(GLFW_KEY_W))
            camera.move((key(GLFW_KEY_LEFT_CONTROL)) ? 2 : 1, 0, 0);
        if (key(GLFW_KEY_A))
            camera.move(0, 0, 1);
        if (key(GLFW_KEY_S))
            camera.move(-1, 0, 0);
        if (key(GLFW_KEY_D))
            camera.move(0, 0, -1);
        if (key(GLFW_KEY_SPACE))
            camera.move(0, 1, 0);
        if (key(GLFW_KEY_LEFT_SHIFT))
            camera.move(0, -1, 0);

        updateCamera(input.dx, input.dy);
        input.dx = input.dy = 0;

        renderer.setTransform(0, 0, 0,
                0, 0, 0,
                1.0f, 1.0f, 1.0f);
    }

    /**
     * Helper method for GLFW input keys
     * @param key The GLFW key ID
     */
    public boolean key(int key) {
        return input.keys[key];
    }

    /**
     * Update the view matrix with the camera details
     */
    public void updateCamera(float dx, float dy) {
        camera.update(dx, dy);
    }

    /**
     * Use the renderer to draw to the window
     */
    public void render() {
        renderer.render(camera);

        // Draw buffer to the screen
        glfwSwapBuffers(handle);
    }

    /**
     * Frees callbacks and destroys the window
     */
    public void destroy() {
        // Free the window callbacks and destroy the window
        input.dispose();
        glfwDestroyWindow(handle);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    /**
     * Set the size of the window
     * @param width The new width
     * @param height The new height
     */
    public void setSize(int width, int height) {
        if (fullscreen) return;
        glfwSetWindowSize(handle, width, height);
        this.autoViewport();
    }

    /**
     * Reset the viewport based on window size
     */
    private void autoViewport() {
        Vector2i size = this.getSize();

        renderer.setViewport(size.x, size.y);
        renderer.render(camera);
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
    public Vector2i getSize() {
        return new Vector2i(this.width, this.height);
    }

    /**
     * @return handle The handle of the window
     */
    public long getHandle() { return handle; }
}
