package com.screendead.minedaft.graphics;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private static final int DRAW_TYPE = GL_STREAM_DRAW;

    private static Image texture;
    private static ArrayList<Integer> vboList = new ArrayList<>();
    private final int vao, vertexCount;

    public Mesh(float[] positions, float[] normals, float[] texCoords, int[] indices) {
        vertexCount = indices.length;
        if (vertexCount == 0) {
            vao = -1;
            return;
        }

        vao = glGenVertexArrays();

        update(positions, normals, texCoords, indices);
    }

    public void update(float[] positions, float[] normals, float[] texCoords, int[] indices) {
        FloatBuffer vertBuffer = null, normsBuffer = null, texBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            vboList = new ArrayList<>();

            glBindVertexArray(vao);

            // Position VBO
            int vbo = glGenBuffers();
            vboList.add(vbo);
            vertBuffer = MemoryUtil.memAllocFloat(positions.length);
            vertBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertBuffer, DRAW_TYPE);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Normals VBO
            vbo = glGenBuffers();
            vboList.add(vbo);
            normsBuffer = MemoryUtil.memAllocFloat(normals.length);
            normsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, normsBuffer, DRAW_TYPE);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

            for (int i = 0; i < texCoords.length; i++) texCoords[i] /= ((float) ((i % 2 == 0) ? texture.getSize().x : texture.getSize().y) / 16.0f);

            // Texture coordinates VBO
            vbo = glGenBuffers();
            vboList.add(vbo);
            texBuffer = MemoryUtil.memAllocFloat(texCoords.length);
            texBuffer.put(texCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, texBuffer, DRAW_TYPE);
            glVertexAttribPointer(2, 2, GL_FLOAT, true, 0, 0);

            // Index VBO
            vbo = glGenBuffers();
            vboList.add(vbo);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, DRAW_TYPE);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (vertBuffer != null) MemoryUtil.memFree(vertBuffer);
            if (normsBuffer != null) MemoryUtil.memFree(normsBuffer);
            if (texBuffer != null) MemoryUtil.memFree(texBuffer);
            if (indicesBuffer != null) MemoryUtil.memFree(indicesBuffer);
        }
    }

    public void update(MeshComponent mc) {
        update(mc.getVertices(), mc.getNormals(), mc.getTexCoords(), mc.getIndices());
    }

    /**
     * Render this mesh to the framebuffer
     */
    public void render() {
        if (texture != null) {
            // Activate first texture
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getID());

            // Draw the mesh
            glBindVertexArray(vao);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            glEnableVertexAttribArray(2);

            glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

            // Restore state
            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glDisableVertexAttribArray(2);
            glBindVertexArray(0);
        }
    }

    /**
     * Clean the memory after removal
     */
    public void cleanup() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboList) {
            glDeleteBuffers(vboId);
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vao);
    }

    public static void setGlobalTexture(Image t) {
        texture = t;
    }

    /**
     * @return vao The vertex array object
     */
    public int getVAO() {
        return vao;
    }

    /**
     * @return vertexCount The number of vertices in the mesh
     */
    public int getVertexCount() {
        return vertexCount;
    }

    public boolean empty() {
        return vertexCount == 0;
    }
}
