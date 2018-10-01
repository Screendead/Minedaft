package com.screendead.minecraft.graphics;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public class Renderer {
    private int id;
    private Matrix4f viewMatrix;

    public Renderer() {
        viewMatrix = new Matrix4f();
    }

    /**
     * Render to the framebuffer
     */
    public void render() {
        // Load the projection matrix
        glLoadMatrixf(viewMatrix.get(new float[16]));

        // Clear the framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, id);

        // Draw a textured quad to the framebuffer
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

        // Unbind the texture
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Initialise OpenGL context for use with this window
     */
    public void init() {
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

    public void setViewport(float width, float height) {
        // Set the viewport
        glViewport(0, 0, (int) width, (int) height);

        // Update the viewMatrix for scaling
        viewMatrix = new Matrix4f().setOrtho2D((float) -width / height, (float) width / height, -1, 1);
    }
}
