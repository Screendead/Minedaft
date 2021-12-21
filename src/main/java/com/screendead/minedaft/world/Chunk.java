package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.Mesh;
import com.screendead.minedaft.graphics.MeshComponent;
import org.lwjgl.stb.STBPerlin;

import java.util.Arrays;

public class Chunk {
    private static final boolean SIMPLE_GENERATION = false;

    private static final float SCALE = 0.005f;

    public int cx, cz;
    int[] blocks;
    int[] lightLevels;
    int[] maxHeight = new int[256];
    private final MeshComponent[] meshComponents = new MeshComponent[16];
    private final Mesh[] meshes = new Mesh[16];

    public Chunk(int cx, int cz, int[] chunkData) {
        this.cx = cx;
        this.cz = cz;

        this.blocks = chunkData;
        this.lightLevels = new int[chunkData.length];
        for (int i = 0; i < lightLevels.length; i++) this.lightLevels[i] = 0;
    }

    public static Chunk generate(int cx, int cz) {
        int[] b = new int[65536];

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 256; k++) {
                    b[flatten(i, j, k)] = generateBlock(cx, cz, i, k, j);
                }
            }
        }

        Chunk c = new Chunk(cx, cz, b);

        c.calculateMaxHeights();
        c.calculateLighting();
        c.generateMeshComponent();

        return c;
    }

    private static int generateBlock(int cx, int cz, int x, int y, int z) {
        float i = (float) (x + (cx << 4)) * Chunk.SCALE,
                j = (float) (z + (cz << 4)) * Chunk.SCALE,
                k = (float) y * Chunk.SCALE;

        if (Chunk.SIMPLE_GENERATION) {
            if (y < 64.0f) return BlockType.STONE.ordinal();
            else return BlockType.AIR.ordinal();
        } else {
            float detail = 1 + (64 * STBPerlin.stb_perlin_turbulence_noise3(i, k, j, 2.0f, 0.5f, 5));
            detail -= 1 + (64 * STBPerlin.stb_perlin_ridge_noise3(i, k, j, 2.0f, 0.5f, 0, 5));

            if (y == 0) return BlockType.BEDROCK.ordinal();
            else if (y <= detail) return BlockType.DEBUG.ordinal();
            else return BlockType.AIR.ordinal();
        }
    }

    private void calculateMaxHeights() {
        for (int subChunk = 15; subChunk >= 0; subChunk--) {
            for (int k = (subChunk + 1) * 16 - 1; k >= subChunk * 16; k--) {
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        int index = Chunk.flatten(i, j, k);
                        int mh = Chunk.flatten(i, j, 0);

                        if (BlockType.values()[blocks[index]].transparent) continue;

                        if (this.maxHeight[mh] < k) {
                            this.maxHeight[mh] = k;
                        }
                    }
                }
            }
        }
    }

    private void calculateLighting() {
        for (int subChunk = 15; subChunk >= 0; subChunk--) {
            for (int k = (subChunk + 1) * 16 - 1; k >= subChunk * 16; k--) {
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        int index = Chunk.flatten(i, j, k);

                        int mh = Chunk.flatten(i, j, 0);

                        if (k >= this.maxHeight[mh]) {
                            this.lightLevels[index] = 16;
                            continue;
                        }

                        int[] surrounding = new int[] {
                                getLightLevel(i + 1, k, j),
                                getLightLevel(i - 1, k, j),
                                getLightLevel(i, k, j + 1),
                                getLightLevel(i, k, j - 1),
                                getLightLevel(i, k + 1, j),
                                getLightLevel(i, k - 1, j),
                        };

                        float max = 0;

                        for (int lvl : surrounding) {
                            if (lvl != -1) {
                                max = Math.max(max, lvl);
                            }
                        }

                        this.lightLevels[index] = (int) max - 1;
                    }
                }
            }
        }
    }

    private void generateMeshComponent() {
        for (int subChunk = 15; subChunk >= 0; subChunk--) {
            if (this.meshComponents[subChunk] != null && this.meshComponents[subChunk].mesh != null) this.meshComponents[subChunk].mesh.cleanup();
            this.meshComponents[subChunk] = new MeshComponent(new float[]{}, new float[]{}, new float[]{}, new float[]{}, new int[]{});

            for (int k = (subChunk + 1) * 16 - 1; k >= subChunk * 16; k--) {
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        int index = Chunk.flatten(i, j, k);
                        int mh = Chunk.flatten(i, j, 0);

                        if (BlockType.values()[blocks[index]].transparent) continue;

                        if (this.maxHeight[mh] == k) {
                            blocks[index] = BlockType.GRASS.ordinal();
                        } else if (blocks[index] == BlockType.DEBUG.ordinal()) {
                            blocks[index] = (k < this.maxHeight[mh] - 4) ? BlockType.STONE.ordinal() : BlockType.DIRT.ordinal();
                        }

//                        if ((this.blocks[index] != BlockType.BEDROCK.ordinal()) && getBlock(i, k + 1, j) == BlockType.AIR.ordinal()) {
//                            this.blocks[index] = BlockType.GRASS.ordinal();
//
//                            int rand = (int) Math.floor(STBPerlin.stb_perlin_noise3((float) cx / j, (float) k / (float) Math.PI, (float) cz / i, 0, 0, 0) * 4 + 2);
//                            for (int l = 0; l < Math.min(rand + 2, k); l++) {
//                                int index2 = index - ((l + 1) << 8);
//                                if (index2 < 2 || this.blocks[index2] == BlockType.AIR.ordinal()) continue;
//                                this.blocks[index2] = BlockType.DIRT.ordinal();
//                            }
//                        }

                        boolean[] faces = new boolean[]{
                                false, // +Z
                                false, // -Z
                                false, // +X
                                false, // -X
                                false, // +Y
                                false // -Y
                        };

                        if (BlockType.values()[getBlock(i, k, j + 1)].transparent) faces[0] = true;
                        if (BlockType.values()[getBlock(i, k, j - 1)].transparent) faces[1] = true;
                        if (BlockType.values()[getBlock(i + 1, k, j)].transparent) faces[2] = true;
                        if (BlockType.values()[getBlock(i - 1, k, j)].transparent) faces[3] = true;
                        if (BlockType.values()[getBlock(i, k + 1, j)].transparent) faces[4] = true;
                        if (BlockType.values()[getBlock(i, k - 1, j)].transparent) faces[5] = true;

                        boolean cont = false;
                        for (int a = 0; a < 6; a++) {
                            if (faces[a]) {
                                cont = true;
                                break;
                            }
                        }

                        if (cont) {
                            int _b = this.blocks[index];
                            MeshComponent _mc = BlockType.values()[_b].getMeshComponent(faces, cx * 16 + i, k, cz * 16 + j, Math.max(lightLevels[index] / 16.0f, 0.0f));
                            this.meshComponents[subChunk].combine(_mc);
                        }
                    }
                }
            }
        }
    }

    public void generateMesh(int subChunk) {
        meshComponents[subChunk].generateMesh();
        this.meshes[subChunk] = meshComponents[subChunk].mesh;
    }

    public void render(int subChunk) {
        if (this.meshes[subChunk] == null) generateMesh(subChunk);
        if (!meshes[subChunk].empty()) meshes[subChunk].render();
    }

    int getLightLevel(int x, int y, int z) {
        if (!BlockType.values()[getBlock(x, y, z)].transparent) return -1;

        if (x == 16) return -1;
        if (x == -1) return -1;
        if (z == 16) return -1;
        if (z == -1) return -1;

        if (y == -1 || y == 256) return 16;

        return lightLevels[flatten(x, z, y)];
    }

    int getBlock(int x, int y, int z) {
        if (x == 16) return generateBlock(cx + 1, cz, 0, y, z);
        if (x == -1) return generateBlock(cx - 1, cz, 15, y, z);
        if (z == 16) return generateBlock(cx, cz + 1, x, y, 0);
        if (z == -1) return generateBlock(cx, cz - 1, x, y, 15);

        if (y == -1 || y == 256) return BlockType.AIR.ordinal();

        return blocks[flatten(x, z, y)];
    }

    static int flatten(int i, int j, int k) {
        return (k << 8) | (i << 4) | j;
    }

    public void cleanup() {
        for (int i = 0; i < 16; i++) {
            if (this.meshes[i] != null) meshes[i].cleanup();
        }
    }
}
