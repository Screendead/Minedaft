package com.screendead.minedaft.graphics;

import java.util.Arrays;

public class MeshComponent {
    private float[] vertices, normals, texCoords;
    private int[] indices;
    private int maxIndex;

    public MeshComponent(float[] vertices, float[] normals, float[] texCoords, int[] indices) {
        this.vertices = vertices;
        this.normals = normals;
        this.texCoords = texCoords;
        this.indices = indices;
    }

    public void combine(MeshComponent m) {
        maxIndex = 0;
        float[] newVertices, newNormals, newTexCoords;
        int[] newIndices;

        newVertices = Arrays.copyOf(vertices, vertices.length + m.vertices.length);
        System.arraycopy(m.vertices, 0, newVertices, vertices.length, m.vertices.length);
        vertices = newVertices;

        newNormals = Arrays.copyOf(normals, normals.length + m.normals.length);
        System.arraycopy(m.normals, 0, newNormals, normals.length, m.normals.length);
        normals = newNormals;

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
        return new Mesh(vertices, normals, texCoords, indices);
    }
}