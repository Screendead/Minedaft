package com.screendead.minecraft.graphics;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_TEXTURE0;
import static org.lwjgl.opengl.GL15.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL15.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.glActiveTexture;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBindTexture;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glDrawElements;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private static ArrayList<Integer> vboList = new ArrayList<>();
    private final int vao, vertexCount;
    private Image texture = null;

    public Mesh(float[] positions, float[] texCoords, int[] indices) {
        FloatBuffer vertBuffer = null, texBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            vertexCount = indices.length;
            vboList = new ArrayList<>();

            vao = glGenVertexArrays();
            glBindVertexArray(vao);

            // Position VBO
            int vbo = glGenBuffers();
            vboList.add(vbo);
            vertBuffer = MemoryUtil.memAllocFloat(positions.length);
            vertBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            vbo = glGenBuffers();
            vboList.add(vbo);
            texBuffer = MemoryUtil.memAllocFloat(texCoords.length);
            texBuffer.put(texCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, texBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Index VBO
            vbo = glGenBuffers();
            vboList.add(vbo);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (vertBuffer != null) MemoryUtil.memFree(vertBuffer);
            if (texBuffer != null) MemoryUtil.memFree(texBuffer);
        }
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

            glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

            // Restore state
            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glBindVertexArray(0);
        }
    }

    /*
    public static Mesh load(String filename) {
        BufferedReader reader;

        List<Float> positions = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> texCoords = new ArrayList<>();

        List<Integer> indices = new ArrayList<>();

        try {
            reader = new BufferedReader(new FileReader("C:/Users/admin/Documents/IntelliJ IDEA Projects/Minecraft/res/models/" + filename));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\s+");

                switch (tokens[0]) {
                    case "v":
                        positions.add(Float.parseFloat(tokens[1]));
                        positions.add(Float.parseFloat(tokens[2]));
                        positions.add(Float.parseFloat(tokens[3]));
                        System.out.println("Added vertex");
                        break;
                    case "vn":
                        normals.add(Float.parseFloat(tokens[1]));
                        normals.add(Float.parseFloat(tokens[1]));
                        normals.add(Float.parseFloat(tokens[1]));
                        System.out.println("Added normal");
                        break;
                    case "vt":
                        texCoords.add(Float.parseFloat(tokens[1]));
                        texCoords.add(Float.parseFloat(tokens[2]));
                        System.out.println("Added texture coord");
                        break;
                    case "f":
                        String[][] iTokens = new String[][] {
                                tokens[1].split("/"),
                                tokens[2].split("/"),
                                tokens[3].split("/")
                        };

//                        // Vertex 1 data
//                        indices.add(Integer.parseInt(iTokens[0][0]) - 1);
//                        indices.add(Integer.parseInt(iTokens[0][1]) - 1);
//                        indices.add(Integer.parseInt(iTokens[0][2]) - 1);
//
//                        // Vertex 2 data
//                        indices.add(Integer.parseInt(iTokens[1][0]) - 1);
//                        indices.add(Integer.parseInt(iTokens[1][1]) - 1);
//                        indices.add(Integer.parseInt(iTokens[1][2]) - 1);
//
//                        // Vertex 3 data
//                        indices.add(Integer.parseInt(iTokens[2][0]) - 1);
//                        indices.add(Integer.parseInt(iTokens[2][1]) - 1);
//                        indices.add(Integer.parseInt(iTokens[2][2]) - 1);

                        indices.add(Integer.parseInt(iTokens[0][0]) - 1);
                        indices.add(Integer.parseInt(iTokens[1][0]) - 1);
                        indices.add(Integer.parseInt(iTokens[2][0]) - 1);

                        indices.add(Integer.parseInt(iTokens[0][1]) - 1);
                        indices.add(Integer.parseInt(iTokens[1][1]) - 1);
                        indices.add(Integer.parseInt(iTokens[2][1]) - 1);

                        indices.add(Integer.parseInt(iTokens[0][2]) - 1);
                        indices.add(Integer.parseInt(iTokens[1][2]) - 1);
                        indices.add(Integer.parseInt(iTokens[2][2]) - 1);

                        System.out.println("Added indices");
                        break;
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        float[] p = new float[positions.size()],
                n = new float[normals.size()],
                t = new float[texCoords.size()];
        int[] i = new int[indices.size()];

        for (int j = 0; j < positions.size(); j++) p[j] = positions.get(j);
        for (int j = 0; j < normals.size(); j++) n[j] = normals.get(j);
        for (int j = 0; j < texCoords.size(); j++) t[j] = texCoords.get(j);
        for (int j = 0; j < indices.size(); j++) i[j] = indices.get(j);

        return new Mesh(p, n, t, i);
    }
    */

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

        // Delete the texture
        texture.cleanup();

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vao);
    }

    public void setTexture(Image texture) {
        this.texture = texture;
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
}
