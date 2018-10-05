package com.screendead.minecraft.graphics;

import org.joml.Vector2i;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Image {
    private int id, width, height;

    public Image(String source) {
        // Get an image for texturing
        IntBuffer w = null, h = null, channels = null;
        try {
            w = MemoryUtil.memAllocInt(1);
            h = MemoryUtil.memAllocInt(1);
            channels = MemoryUtil.memAllocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(false);

            // Load the texture
            ByteBuffer img = STBImage.stbi_load("C:/Users/admin/Documents/IntelliJ IDEA Projects/Minecraft/res/img/" + source, w, h, channels, 4);
            if (img == null) throw new RuntimeException("Texture " + source + " failed to load.");

            // Store width and height values
            width = w.get();
            height = h.get();

            // Create texture ID
            id = glGenTextures();
            if (id == 0) throw new RuntimeException("Failed to allocate texture ID.");

            glActiveTexture(GL_TEXTURE0);

            // Bind the texture
            this.bind();

            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            // Set texture parameters
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            // Flip the buffer for reading
            img.flip();

            // Pass texture data to the graphics card
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, img);

            // Mipmap the texture
            glGenerateMipmap(GL_TEXTURE_2D);

            // Unbind the texture
            Image.unbind();
        } finally {
            // Free memory locations of buffers
            if (w != null) MemoryUtil.memFree(w);
            if (h != null) MemoryUtil.memFree(h);
            if (channels != null) MemoryUtil.memFree(channels);
        }
    }

    /**
     * @return The texture ID
     */
    public int getID() {
        return id;
    }

    /**
     * @return A Vector2i storing width and height
     */
    public Vector2i getSize() {
        return new Vector2i(width, height);
    }

    /**
     * Bind this image to GL_TEXTURE_2D
     */
    public void bind() { glBindTexture(GL_TEXTURE_2D, id); }

    /**
     * Unbind GL_TEXTURE_2D
     */
    public static void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Clean the memory after removal
     */
    public void cleanup() {
        glDeleteTextures(id);
    }
}
