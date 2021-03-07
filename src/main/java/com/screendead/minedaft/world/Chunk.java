package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.Mesh;
import com.screendead.minedaft.graphics.MeshComponent;
import org.lwjgl.stb.STBPerlin;

public class Chunk {
    private static final boolean SIMPLE_GENERATION = false;
    private static final boolean LERP_GENERATION = false;
    private static final int LERP = 2; // 4x4 interpolation

    private static final float SCALE = 0.005f;

    public int cx, cz;
    int[] blocks;
    int[] maxHeight = new int[256];
    private final MeshComponent[] meshComponents = new MeshComponent[16];
    private final Mesh[] meshes = new Mesh[16];

    public Chunk(int cx, int cz, int[] chunkData) {
        this.cx = cx;
        this.cz = cz;

        this.blocks = chunkData;
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
            float detail = 1 + 127.0f * STBPerlin.stb_perlin_turbulence_noise3(i, k, j, 2.0f, 0.5f, 5);

            if (y == 0) return BlockType.BEDROCK.ordinal();
            else if (y < detail) return BlockType.STONE.ordinal();
            else return BlockType.AIR.ordinal();
        }
    }

    private void generateMeshComponent() {
        for (int subChunk = 0; subChunk < 16; subChunk++) {
            this.meshComponents[subChunk] = new MeshComponent(new float[]{}, new float[]{}, new float[]{}, new int[]{});

            for (int k = subChunk * 16; k < (subChunk + 1) * 16; k++) {
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        int index = Chunk.flatten(i, j, k);
                        int mh = Chunk.flatten(i, j, 0);
                        if (BlockType.values()[blocks[index]].transparent) continue;
                        if (this.maxHeight[mh] < k) this.maxHeight[mh] = k;


                        if ((this.blocks[index] != BlockType.BEDROCK.ordinal()) && getBlock(i, k + 1, j) == BlockType.AIR.ordinal()) {
                            this.blocks[index] = BlockType.GRASS.ordinal();

                            int rand = (int) Math.floor(STBPerlin.stb_perlin_noise3((float) cx / j, (float) k / (float) Math.PI, (float) cz / i, 0, 0, 0) * 4 + 2);
                            for (int l = 0; l < Math.min(rand + 2, k); l++) {
                                int index2 = index - ((l + 1) << 8);
                                if (index2 < 2 || this.blocks[index2] == BlockType.AIR.ordinal()) continue;
                                this.blocks[index2] = BlockType.DIRT.ordinal();
                            }
                        }

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

                        if (cont)
                            this.meshComponents[subChunk].combine(BlockType.values()[this.blocks[index]].getMeshComponent(faces, cx * 16 + i, k, cz * 16 + j));
                    }
                }
            }
        }
    }

    public void generateMesh(int subChunk) {
        this.meshes[subChunk] = meshComponents[subChunk].toMesh();
    }

    public void render(int subChunk) {
        if (this.meshes[subChunk] == null) generateMesh(subChunk);
        if (!meshes[subChunk].empty()) meshes[subChunk].render();
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
