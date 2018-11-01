package com.screendead.minedaft.graphics;

import java.util.Arrays;

public class MeshComponent {
    private float[] vertices, texCoords;
    private int[] indices;
    private Image texture;

    public MeshComponent(float[] vertices, float[] texCoords, int[] indices) {
        this.vertices = vertices;
        this.texCoords = texCoords;
        this.indices = indices;
        this.texture = texture;
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

        newIndices = Arrays.copyOf(indices, indices.length + m.indices.length);
        System.arraycopy(m.indices, 0, newIndices, indices.length, m.indices.length);
        indices = newIndices;
    }

    public Mesh toMesh() {
//        for (float v : vertices) System.out.print(v + ", ");
//        System.out.println();
//        System.out.println();
//        for (float t : texCoords) System.out.print(t + ", ");
//        System.out.println();
//        System.out.println();
//        for (int i : indices) System.out.print(i + ", ");
//        System.out.println();
//        System.out.println();

        return new Mesh(vertices, texCoords, indices);
    }
}
