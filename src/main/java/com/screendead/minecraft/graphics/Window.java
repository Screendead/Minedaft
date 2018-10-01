package com.screendead.minecraft.graphics;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private long handle;
    private int id;
    private Matrix4f viewMatrix = new Matrix4f();

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

        // Make the OpenGL context current
        glfwMakeContextCurrent(handle);
        this.initOpenGL();
        // Enable v-sync
        glfwSwapInterval(1);

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(handle, (handle, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(handle, true); // We will detect this in the rendering loop
        });

        // Setup a resizing callback.
        glfwSetWindowSizeCallback(handle, (handle, w, h) -> {
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            int minSize = 200;
            glfwSetWindowSizeLimits(handle, minSize, minSize, Math.max(vidmode.width(), minSize), Math.max(Math.min(w, vidmode.height()), minSize));
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
     * Initialise OpenGL context for use with this window
     */
    public void initOpenGL() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Enable 2D texturing
        glEnable(GL_TEXTURE_2D);

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Set an image file for texturing
     * @param source The path to the file to be used
     */
    public void initImage(String source) {
        // Get an image for texturing
        IntBuffer w = BufferUtils.createIntBuffer(1),
                h = BufferUtils.createIntBuffer(1),
                channels = BufferUtils.createIntBuffer(1);
        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer img = STBImage.stbi_load(source, w, h, channels, 4);
        if (img == null) throw new RuntimeException("Failed to load texture.");

        int width = w.get(),
                height = h.get();
        id = glGenTextures();
        if (id == 0) throw new RuntimeException("Failed to allocate texture ID.");

        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, id);

        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Flip the buffer for reading
        img.flip();

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, img);

        // Unbind the texture
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Render to the window
     */
    public void render() {
        float[] m = new float[16];
        viewMatrix.get(m);
        glLoadMatrixf(m);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, id);

        // Draw a quad to the framebuffer
        glBegin(GL_QUADS);
            glTexCoord2f(0.0f, 0.0f);
            glVertex2f(-0.5f, -0.5f);

            glTexCoord2f(0.0f, 1.0f);
            glVertex2f(-0.5f,  0.5f);

            glTexCoord2f(1.0f, 1.0f);
            glVertex2f( 0.5f,  0.5f);

            glTexCoord2f(1.0f, 0.0f);
            glVertex2f( 0.5f, -0.5f);
        glEnd();

        // Draw buffer to the screen
        glfwSwapBuffers(handle);

        // Unbind the texture
        glBindTexture(GL_TEXTURE_2D, 0);
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
    public void autoViewport() {
        Vector2i size = this.getSize();

        // Set the viewport
        glViewport(0, 0, size.x, size.y);

        // Update the viewMatrix for scaling
        viewMatrix = new Matrix4f().setOrtho2D((float) -size.x / size.y, (float) size.x / size.y, -1, 1).scale(2);
    }

    /**
     * Centre the window
     */
    public void centre() {
        Vector2i size = this.getSize();

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        // Center the window
        glfwSetWindowPos(
                handle,
                (vidmode.width() - size.x) / 2,
                (vidmode.height() - size.y) / 2
        );
    }

    /**
     * Get the size of the window in pixels
     * @return the size of the window
     */
    public Vector2i getSize() {
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
