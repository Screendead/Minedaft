package com.screendead.minedaft.graphics;

import java.util.Arrays;

public class MeshComponent {
    private float[] vertices, texCoords;
    private int[] indices;
    private int maxIndex;

    public MeshComponent(float[] vertices, float[] texCoords, int[] indices) {
        this.vertices = vertices;
        this.texCoords = texCoords;
        this.indices = indices;
    }

    public void combine(MeshComponent m) {
        float[] newVertices, newTexCoords;
        int[] newIndices;

        newVertices = Arrays.copyOf(vertices, vertices.length + m.vertices.length);
        System.arraycopy(m.vertices, 0, newVertices, vertices.length, m.vertices.length);
        vertices = newVertices;

        newTexCoords = Arrays.copyOf(texCoords, texCoords.length + m.texCoords.length);
        System.arraycopy(m.texCoords, 0, newTexCoords, texCoords.length, m.texCoords.length);
        texCoords = newTexCoords;

//        newIndices = new int[indices.length + m.indices.length];
//        for (int i : indices) if (i > maxIndex) maxIndex = i;
//        for (int i = 0; i < indices.length; i++) newIndices[i] = indices[i];
//        for (int i = indices.length; i < newIndices.length; i++) newIndices[i] = 1 + m.indices[i - indices.length] + maxIndex;

        newIndices = new int[m.indices.length + indices.length];
        for (int i : m.indices) if (i > maxIndex) maxIndex = i;
        for (int i = 0; i < m.indices.length; i++) newIndices[i] = m.indices[i];
        for (int i = m.indices.length; i < newIndices.length; i++) newIndices[i] = indices[i - m.indices.length] + maxIndex + 1;

//        newIndices = Arrays.copyOf(indices, indices.length + m.indices.length);
//        System.arraycopy(m.indices, 0, newIndices, indices.length, m.indices.length);
        indices = newIndices;

//        for (int i = 0; i < indices.length / 6; i++) {
//            for (int j = 0; j < 6; j++) System.out.print(indices[i * 6 + j] + ", ");
//            System.out.println();
//        }
    }

    public Mesh toMesh() {
        return new Mesh(vertices, texCoords, indices);
    }
}
