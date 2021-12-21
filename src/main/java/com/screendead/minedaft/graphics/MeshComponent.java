package com.screendead.minedaft.graphics;

import java.util.Arrays;

public class MeshComponent {
    private float[] vertices, normals, texCoords, shadows;
    private int[] indices;
    public Mesh mesh;

    public MeshComponent(float[] vertices, float[] normals, float[] texCoords, float[] shadows, int[] indices) {
        this.vertices = vertices;
        this.normals = normals;
        this.texCoords = texCoords;
        this.shadows = shadows;
        this.indices = indices;
    }

    public MeshComponent() {
        this(new float[] {}, new float[] {}, new float[] {}, new float[] {}, new int[] {});
    }

    public void combine(MeshComponent m) {
        int maxIndex = 0;
        float[] newVertices, newNormals, newTexCoords, newShadows;
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

        newShadows = Arrays.copyOf(shadows, shadows.length + m.shadows.length);
        System.arraycopy(m.shadows, 0, newShadows, shadows.length, m.shadows.length);
        shadows = newShadows;

        for (int i : indices) if (i >= maxIndex) maxIndex = i + 1;
        for (int i = 0; i < m.indices.length; i++) m.indices[i] += maxIndex;
        newIndices = Arrays.copyOf(indices, indices.length + m.indices.length);
        System.arraycopy(m.indices, 0, newIndices, indices.length, m.indices.length);
        indices = newIndices;
    }

    public void generateMesh() {
        if (this.mesh != null) this.mesh.cleanup();
        this.mesh = new Mesh(vertices, normals, texCoords, shadows, indices);
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getNormals() {
        return normals;
    }

    public float[] getTexCoords() {
        return texCoords;
    }

    public float[] getShadows() {
        return shadows;
    }

    public int[] getIndices() {
        return indices;
    }
}
