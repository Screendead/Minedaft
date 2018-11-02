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
        maxIndex = 0;
        float[] newVertices, newTexCoords;
        int[] newIndices;

        newVertices = Arrays.copyOf(vertices, vertices.length + m.vertices.length);
        System.arraycopy(m.vertices, 0, newVertices, vertices.length, m.vertices.length);
        vertices = newVertices;

        newTexCoords = Arrays.copyOf(texCoords, texCoords.length + m.texCoords.length);
        System.arraycopy(m.texCoords, 0, newTexCoords, texCoords.length, m.texCoords.length);
        texCoords = newTexCoords;

        for (int i : indices) if (i >= maxIndex) maxIndex = i + 1;
        for (int i = 0; i < m.indices.length; i++) m.indices[i] += maxIndex;
        newIndices = Arrays.copyOf(indices, indices.length + m.indices.length);
        System.arraycopy(m.indices, 0, newIndices, indices.length, m.indices.length);
        indices = newIndices;
    }

    public Mesh toMesh() {
        return new Mesh(vertices, texCoords, indices);
    }
}
