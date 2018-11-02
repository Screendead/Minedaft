package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.MeshComponent;

import java.util.ArrayList;

public enum BlockType {
    AIR(0, "air", new float[][] {}),
    GRASS(1, "grass", new float[][] {
            new float[] { // +Z
                    0.5f, 0.5f,
                    0.5f, 1.0f,
                    0.0f, 0.5f,
                    0.0f, 1.0f
            }, new float[] { // -Z
                    0.5f, 0.5f,
                    0.5f, 1.0f,
                    0.0f, 0.5f,
                    0.0f, 1.0f
            }, new float[] { // +X
                    0.5f, 0.5f,
                    0.5f, 1.0f,
                    0.0f, 0.5f,
                    0.0f, 1.0f
            }, new float[] { // -X
                    0.5f, 0.5f,
                    0.5f, 1.0f,
                    0.0f, 0.5f,
                    0.0f, 1.0f
            }, new float[] { // +Y
                    0.0f, 0.0f,
                    0.0f, 0.5f,
                    0.5f, 0.0f,
                    0.5f, 0.5f
            }, new float[] { // -Y
                    0.5f, 1.0f,
                    1.0f, 1.0f,
                    0.5f, 0.5f,
                    1.0f, 0.5f
            }
    });

    private static final float[][] vertices = new float[][] {
            new float[] { // +Z
                    0.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 1.0f
            }, new float[] { // -Z
                    1.0f, 0.0f, 0.0f,
                    1.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f
            }, new float[] { // +X
                    1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 1.0f, 0.0f
            }, new float[] { // -X
                    0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 1.0f
            }, new float[] { // +Y
                    0.0f, 1.0f, 0.0f,
                    1.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f
            }, new float[] { // -Y
                    0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f
            }
    };

    private static int[][] indices = new int[][] {
            new int[] { // +Z
                    0, 2, 1, 1, 2, 3
            }, new int[] { // -Z
                    4, 6, 5, 5, 6, 7
            }, new int[] { // +X
                    8, 10, 9, 9, 10, 11
            }, new int[] { // -X
                    12, 14, 13, 13, 14, 15
            }, new int[] { // +Y
                    16, 18, 17, 17, 18, 19
            }, new int[] { // -Y
                    20, 22, 21, 21, 22, 23
            }
    };

    private int id;
    private String name;
    private float[][] texCoords;

    BlockType(int id, String name, float[][] texCoords) {
        this.id = id;
        this.name = name;
        this.texCoords = texCoords;
    }

    public MeshComponent getMeshComponent(boolean[] faces, int x, int y, int z) {
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < faces.length; i++) if (faces[i]) index.add(i);

        float[] v = new float[index.size() * 12 * 2];
        float[] t = new float[index.size() * 8 * 2];
        int[] mi = new int[index.size() * 6];

        for (int i = 0; i < index.size(); i++) {
            int position = index.get(i);

            for (int j = 0; j < 12; j++) {
                int offset = (j % 3 == 0) ? x : (j % 3 == 1) ? y : z;
                v[i * 12 + j] = vertices[position][j] + offset;
            }

            for (int j = 0; j < 8; j++) {
                t[i * 8 + j] = texCoords[position][j];
            }

            for (int j = 0; j < 6; j++) {
                mi[i * 6 + j] = indices[position][j];
            }
        }

        return new MeshComponent(v, t, mi);
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}
