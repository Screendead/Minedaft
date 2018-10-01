package com.screendead.minecraft.graphics;

import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public class Image {
    private int id, width, height;

    public Image(String source) {
        // Get an image for texturing
        IntBuffer w = BufferUtils.createIntBuffer(1),
                h = BufferUtils.createIntBuffer(1),
                channels = BufferUtils.createIntBuffer(1);
        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer img = STBImage.stbi_load(source, w, h, channels, 4);
        if (img == null) throw new RuntimeException("Failed to load texture.");

        // Store width and height values
        width = w.get();
        height = h.get();

        // Create texture ID
        id = glGenTextures();
        if (id == 0) throw new RuntimeException("Failed to allocate texture ID.");

        // Bind the texture
        this.bind();

        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Flip the buffer for reading
        img.flip();

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, img);

        // Unbind the texture
        Image.unbind();
    }

    public int getID() {
        return id;
    }

    public Vector2i getSize() {
        return new Vector2i(width, height);
    }

    /**
     * Bind this image to GL_TEXTURE_2D
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    /**
     * Unbind GL_TEXTURE_2D
     */
    public static void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
