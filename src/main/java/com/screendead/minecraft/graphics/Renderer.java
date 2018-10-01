package com.screendead.minecraft.graphics;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class Renderer {
    private Image texture;
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
        texture.bind();

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
        Image.unbind();
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

        texture = new Image("C:/Users/admin/Documents/IntelliJ IDEA Projects/Minecraft/res/img/heart.png");

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void setViewport(float width, float height) {
        // Set the viewport
        glViewport(0, 0, (int) width, (int) height);

        // Update the viewMatrix for scaling
        viewMatrix = new Matrix4f().setOrtho2D(-width / height, width / height, -1, 1);
    }
}
