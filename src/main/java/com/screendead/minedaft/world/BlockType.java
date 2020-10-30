package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.MeshComponent;

import java.util.ArrayList;

public enum BlockType {
    AIR(0, "air", true, new float[][] {}),
    GRASS(1, "grass", false, new float[][] {
            new float[] { // +Z
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    0.0f, 0.0f,
            }, new float[] { // -Z
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    0.0f, 0.0f,
            }, new float[] { // +X
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    0.0f, 0.0f,
            }, new float[] { // -X
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    0.0f, 0.0f,
            }, new float[] { // +Y
                    0.0f, 2.0f,
                    0.0f, 1.0f,
                    1.0f, 2.0f,
                    1.0f, 1.0f,
            }, new float[] { // -Y
                    1.0f, 0.0f,
                    2.0f, 0.0f,
                    1.0f, 1.0f,
                    2.0f, 1.0f,
            }
    }),
    DIRT(2, "dirt", false, new float[][] {
            new float[] { // +Z
                    1.0f, 0.0f,
                    2.0f, 0.0f,
                    1.0f, 1.0f,
                    2.0f, 1.0f,
            }, new float[] { // -Z
                    1.0f, 0.0f,
                    2.0f, 0.0f,
                    1.0f, 1.0f,
                    2.0f, 1.0f,
            }, new float[] { // +X
                    1.0f, 0.0f,
                    2.0f, 0.0f,
                    1.0f, 1.0f,
                    2.0f, 1.0f,
            }, new float[] { // -X
                    1.0f, 0.0f,
                    2.0f, 0.0f,
                    1.0f, 1.0f,
                    2.0f, 1.0f,
            }, new float[] { // +Y
                    1.0f, 0.0f,
                    2.0f, 0.0f,
                    1.0f, 1.0f,
                    2.0f, 1.0f,
            }, new float[] { // -Y
                    1.0f, 0.0f,
                    2.0f, 0.0f,
                    1.0f, 1.0f,
                    2.0f, 1.0f,
            }
    }),
    STONE(3, "stone", false, new float[][] {
            new float[] { // +Z
                    2.0f, 2.0f,
                    2.0f, 1.0f,
                    1.0f, 2.0f,
                    1.0f, 1.0f,
            }, new float[] { // -Z
                    2.0f, 2.0f,
                    2.0f, 1.0f,
                    1.0f, 2.0f,
                    1.0f, 1.0f,
            }, new float[] { // +X
                    2.0f, 2.0f,
                    2.0f, 1.0f,
                    1.0f, 2.0f,
                    1.0f, 1.0f,
            }, new float[] { // -X
                    2.0f, 2.0f,
                    2.0f, 1.0f,
                    1.0f, 2.0f,
                    1.0f, 1.0f,
            }, new float[] { // +Y
                    2.0f, 2.0f,
                    2.0f, 1.0f,
                    1.0f, 2.0f,
                    1.0f, 1.0f,
            }, new float[] { // -Y
                    2.0f, 2.0f,
                    2.0f, 1.0f,
                    1.0f, 2.0f,
                    1.0f, 1.0f,
            }
    }),
    BEDROCK(4, "bedrock", false, new float[][] {
            new float[] { // +Z
                    3.0f, 1.0f,
                    3.0f, 0.0f,
                    2.0f, 1.0f,
                    2.0f, 0.0f,
            }, new float[] { // -Z
                    3.0f, 1.0f,
                    3.0f, 0.0f,
                    2.0f, 1.0f,
                    2.0f, 0.0f,
            }, new float[] { // +X
                    3.0f, 1.0f,
                    3.0f, 0.0f,
                    2.0f, 1.0f,
                    2.0f, 0.0f,
            }, new float[] { // -X
                    3.0f, 1.0f,
                    3.0f, 0.0f,
                    2.0f, 1.0f,
                    2.0f, 0.0f,
            }, new float[] { // +Y
                    3.0f, 1.0f,
                    3.0f, 0.0f,
                    2.0f, 1.0f,
                    2.0f, 0.0f,
            }, new float[] { // -Y
                    3.0f, 1.0f,
                    3.0f, 0.0f,
                    2.0f, 1.0f,
                    2.0f, 0.0f,
            }
    }),
    TNT(5, "TNT", false, new float[][] {
            new float[] { // +Z
                    1.0f, 3.0f,
                    1.0f, 2.0f,
                    2.0f, 3.0f,
                    2.0f, 2.0f,
            }, new float[] { // -Z
                    1.0f, 3.0f,
                    1.0f, 2.0f,
                    2.0f, 3.0f,
                    2.0f, 2.0f,
            }, new float[] { // +X
                    1.0f, 3.0f,
                    1.0f, 2.0f,
                    2.0f, 3.0f,
                    2.0f, 2.0f,
            }, new float[] { // -X
                    1.0f, 3.0f,
                    1.0f, 2.0f,
                    2.0f, 3.0f,
                    2.0f, 2.0f,
            }, new float[] { // +Y
                    0.0f, 3.0f,
                    0.0f, 2.0f,
                    1.0f, 3.0f,
                    1.0f, 2.0f,
            }, new float[] { // -Y
                    2.0f, 3.0f,
                    2.0f, 2.0f,
                    3.0f, 3.0f,
                    3.0f, 2.0f,
            }
    }),
    ICE(6, "ice", true, new float[][] {
            new float[] { // +Z
                    3.0f, 2.0f,
                    3.0f, 1.0f,
                    2.0f, 2.0f,
                    2.0f, 1.0f,
            }, new float[] { // -Z
                    3.0f, 2.0f,
                    3.0f, 1.0f,
                    2.0f, 2.0f,
                    2.0f, 1.0f,
            }, new float[] { // +X
                    3.0f, 2.0f,
                    3.0f, 1.0f,
                    2.0f, 2.0f,
                    2.0f, 1.0f,
            }, new float[] { // -X
                    3.0f, 2.0f,
                    3.0f, 1.0f,
                    2.0f, 2.0f,
                    2.0f, 1.0f,
            }, new float[] { // +Y
                    3.0f, 2.0f,
                    3.0f, 1.0f,
                    2.0f, 2.0f,
                    2.0f, 1.0f,
            }, new float[] { // -Y
                    3.0f, 2.0f,
                    3.0f, 1.0f,
                    2.0f, 2.0f,
                    2.0f, 1.0f,
            }
    }),
    DEBUG(8888, "debug", false, new float[][] {
            new float[] { // +Z
                    15.0f, 16.0f,
                    15.0f, 15.0f,
                    16.0f, 16.0f,
                    16.0f, 15.0f,
            }, new float[] { // -Z
                    15.0f, 16.0f,
                    15.0f, 15.0f,
                    16.0f, 16.0f,
                    16.0f, 15.0f,
            }, new float[] { // +X
                    15.0f, 16.0f,
                    15.0f, 15.0f,
                    16.0f, 16.0f,
                    16.0f, 15.0f,
            }, new float[] { // -X
                    15.0f, 16.0f,
                    15.0f, 15.0f,
                    16.0f, 16.0f,
                    16.0f, 15.0f,
            }, new float[] { // +Y
                    15.0f, 16.0f,
                    15.0f, 15.0f,
                    16.0f, 16.0f,
                    16.0f, 15.0f,
            }, new float[] { // -Y
                    15.0f, 16.0f,
                    15.0f, 15.0f,
                    16.0f, 16.0f,
                    16.0f, 15.0f,
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

    private static final float[][] normals = new float[][] {
            new float[] { // +Z
                     0.0f,  0.0f, -1.0f
            }, new float[] { // -Z
                     0.0f,  0.0f,  1.0f
            }, new float[] { // +X
                    -1.0f,  0.0f,  0.0f
            }, new float[] { // -X
                     1.0f,  0.0f,  0.0f
            }, new float[] { // +Y
                     0.0f, -1.0f,  0.0f
            }, new float[] { // -Y
                     0.0f,  1.0f,  0.0f
            }
    };

    private static final int[] indices = new int[] {
            0, 2, 1, 1, 2, 3,
            4, 6, 5, 5, 6, 7,
            8, 10, 9, 9, 10, 11,
            12, 14, 13, 13, 14, 15,
            16, 18, 17, 17, 18, 19,
            20, 22, 21, 21, 22, 23
    };

    private final int id;
    private final String name;
    public final boolean transparent;
    private final float[][] texCoords;

    BlockType(int id, String name, boolean transparent, float[][] texCoords) {
        this.id = id;
        this.name = name;
        this.transparent = transparent;
        this.texCoords = texCoords;
    }

    public MeshComponent getMeshComponent(boolean[] faces, int x, int y, int z) {
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < 6; i++) if (faces[i]) index.add(i);

        float[] v = new float[index.size() * 12];
        float[] n = new float[index.size() * 12];
        float[] t = new float[index.size() * 8];
        int[] mi = new int[index.size() * 6];

        for (int i = 0; i < index.size(); i++) {
            int position = index.get(i);

            for (int j = 0; j < 12; j++) {
                int offset = (j % 3 == 0) ? x : (j % 3 == 1) ? y : z;
                v[i * 12 + j] = vertices[position][j] + offset;
            }

            for (int j = 0; j < 12; j++) {
                n[i * 12 + j] = normals[position][j % 3];
            }

            for (int j = 0; j < 8; j++) {
                t[i * 8 + j] = texCoords[position][j];
            }

            for (int j = 0; j < index.size() * 6; j++) mi[j] = indices[j];
        }

        return new MeshComponent(v, n, t, mi);
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}
