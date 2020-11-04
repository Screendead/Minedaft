package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.Mesh;
import com.screendead.minedaft.graphics.MeshComponent;
import org.lwjgl.stb.STBPerlin;

public class Chunk {
    private static final boolean SIMPLE_GENERATION = false;

    private static final float SCALE = 0.005f;
    private static final float D_SCALE = 0.005f;

    public int cx, cz;
    int[] blocks;
    int[] maxHeight = new int[256];
    private MeshComponent meshComponent = null;
    private Mesh mesh = null;

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
        if (Chunk.SIMPLE_GENERATION) {
            if (y < 8) return BlockType.STONE.ordinal();
            else return BlockType.AIR.ordinal();
        } else {
            float i = (float) (x + (cx << 4)) * Chunk.SCALE,
                    j = (float) (z + (cz << 4)) * Chunk.SCALE,
                    k = (float) y * Chunk.D_SCALE;

            float height = 64.0f * STBPerlin.stb_perlin_turbulence_noise3(i * 0.25f, k * 0.25f, j * 0.25f, 2, 0.5f, 6);
            float detail = 256.0f * STBPerlin.stb_perlin_turbulence_noise3(i, k, j, 2, 0.5f, 6);
            float detailDivisor = 16.0f * STBPerlin.stb_perlin_ridge_noise3(i, 0, j, 2, 0.5f, 0, 4);

            if (y == 0) return BlockType.BEDROCK.ordinal();
            else if (y <= height + detail / detailDivisor) return BlockType.STONE.ordinal();
            else return BlockType.AIR.ordinal();
        }
    }

    private void generateMeshComponent() {
        this.meshComponent = new MeshComponent(new float[] {}, new float[] {}, new float[] {}, new int[] {});

        for (int k = 255; k >= 0; k--) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    int index = Chunk.flatten(i, j, k);
                    int mh = Chunk.flatten(i, j, 0);
                    if (BlockType.values()[blocks[index]].transparent) continue;
                    if (this.maxHeight[mh] < k) this.maxHeight[mh] = k;


                    if ((this.blocks[index] != BlockType.BEDROCK.ordinal()) && getBlock(i, k + 1, j) == BlockType.AIR.ordinal()) {
                        this.blocks[index] = BlockType.GRASS.ordinal();

                        int rand = (int) Math.floor(STBPerlin.stb_perlin_noise3((float) cx / j, (float) k / (float) Math.PI, (float) cz / i, 0, 0 ,0) * 4 + 2);
                        for (int l = 0; l < Math.min(rand + 2, k); l++) {
                            int index2 = index - ((l + 1) << 8);
                            if (index2 < 2 || this.blocks[index2] == BlockType.AIR.ordinal()) continue;
                            this.blocks[index2] = BlockType.DIRT.ordinal();
                        }
                    }

                    boolean[] faces = new boolean[] {
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

                    if (cont) this.meshComponent.combine(BlockType.values()[this.blocks[index]].getMeshComponent(faces, cx * 16 + i, k, cz * 16 + j));
                }
            }
        }
    }

    public void generateMesh() {
        this.mesh = meshComponent.toMesh();
    }

    public void render() {
        if (this.mesh == null) generateMesh();
        mesh.render();
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
        if (this.mesh != null) mesh.cleanup();
    }
}
