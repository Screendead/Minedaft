package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.Mesh;
import com.screendead.minedaft.graphics.MeshComponent;
import com.screendead.minedaft.performance.ChunkManager;
import org.joml.Vector3i;
import org.lwjgl.stb.STBPerlin;

import java.util.Arrays;

public class Chunk {
    public static final Chunk EMPTY = new Chunk();

    private static float SCALE = 0.005f;
    private static float D_SCALE = 0.005f;

    public int cx, cz;
    Block[] blocks = new Block[65536];
    int[] maxHeight = new int[256];
    private MeshComponent meshComponent = null;
    private Mesh mesh = null;
    private boolean empty = false;

    public Chunk(int cx, int cz, Block[] chunkData) {
        this.cx = cx;
        this.cz = cz;

        this.blocks = chunkData;
    }

    private Chunk() {
        this.empty = true;
    }

    public static Chunk generate(int cx, int cz) {
        Block[] b = new Block[65536];

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 256; k++) {
                    b[flatten(i, j, k)] = new Block(generateBlock(cx, cz, i, k, j), new Vector3i(i + (cx << 4), k, j + (cz << 4)));
                }
            }
        }

//        for (int i = 0; i < 16; i++) {
//            for (int j = 0; j < 16; j++) {
////                float x = (float) (i + (cx << 4)) * Chunk.SCALE,
////                        z = (float) (j + (cz << 4)) * Chunk.SCALE;
////                float height = 0.0f;
////                float height = 64.0f * (STBPerlin.stb_perlin_noise3(x, 0, z, 0, 0, 0) - 0.5f);
//                for (int k = 0; k < 256; k++) {
//                    int index = flatten(i, j, k);
//////                    float detail = 64.0f;
//////                    float detail = 64.0f * (STBPerlin.stb_perlin_noise3(x, k * dScale, z, 0, 0, 0) - 0.5f);
//////                    float detail = 64.0f * (STBPerlin.stb_perlin_turbulence_noise3(x, k * Chunk.D_SCALE, z, 1.2f, 0.35f, 3) + 1);
////                    float detail = 128.0f * (STBPerlin.stb_perlin_turbulence_noise3(x, k * Chunk.D_SCALE, z, 2.0f, 0.5f, 5) - 0.5f);
////
//////                    float detail = STBPerlin.stb_perlin_turbulence_noise3(x, k * Chunk.D_SCALE, z, 1.5f, 0.5f, 12);
//////                    float detail2 = STBPerlin.stb_perlin_noise3(x * 10.0f + 1000.0f, k * Chunk.D_SCALE * 10.0f + 1000.0f, z * 10.0f + 1000.0f, 0, 0, 0);
////
////                    BlockType type;
////
////                    if (k == 0) type = BlockType.BEDROCK;
//////                    else if (detail < 0.5f) type = BlockType.STONE;
//////                    if (detail < 0.5f) type = BlockType.STONE;
//////                    if (detail < 0.5f && detail2 < 0.0f && Math.pow(cx * 16 + i - 128.0f, 2) + Math.pow(k - 128.0f, 2) + Math.pow(cz * 16 + j - 128.0f, 2) <= Math.pow(128.0f, 2)) type = BlockType.DEBUG;
////                    else if (k*k <= height*height + detail*detail) type = BlockType.STONE;
////                    else if (k <= height - detail) type = (Math.random() < 0.1) ? BlockType.TNT : BlockType.STONE;
////                    else type = BlockType.AIR;
////
//                    b[index] = new Block(generateBlock(cx, cz, i, k, j), new Vector3i(i + (cx << 4), k, j + (cz << 4)));
//                }
//            }
//        }

        Chunk c = new Chunk(cx, cz, b);
        c.generateMeshComponent();

        return c;
    }

    private static BlockType generateBlock(int cx, int cz, int x, int y, int z) {
        float i = (float) (x + (cx << 4)) * Chunk.SCALE,
                j = (float) (z + (cz << 4)) * Chunk.SCALE,
            k = (float) y * Chunk.D_SCALE;

        float height = 64.0f * (STBPerlin.stb_perlin_noise3(i, 0, j, 0, 0, 0) - 0.5f);
        float detail = 128.0f * (STBPerlin.stb_perlin_turbulence_noise3(i, k, j, 2.0f, 0.5f, 5) - 0.5f);

        if (k == 0) return BlockType.BEDROCK;
        else if (detail < 0.3f) return BlockType.STONE;
        else return BlockType.AIR;
    }

    private void generateMeshComponent() {
        this.meshComponent = new MeshComponent(new float[] {}, new float[] {}, new float[] {}, new int[] {});

        if (!this.isEmpty()) {
            for (int k = 255; k >= 0; k--) {
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        int index = Chunk.flatten(i, j, k);
                        int mh = Chunk.flatten(i, j, 0);
                        if (this.blocks[index].getType().transparent) continue;
                        if (this.maxHeight[mh] < k) this.maxHeight[mh] = k;


                        if ((this.blocks[index].getType() != BlockType.BEDROCK) && getBlock(i, k + 1, j) == BlockType.AIR) {
                            this.blocks[index].setType(BlockType.GRASS);

                            int rand = (int) Math.floor(STBPerlin.stb_perlin_noise3((float) cx / j, (float) k / (float) Math.PI, (float) cz / i, 0, 0 ,0) * 4 + 2);
                            for (int l = 0; l < Math.min(rand + 2, k); l++) {
                                int index2 = index - ((l + 1) << 8);
                                if (index2 < 2 || this.blocks[index2].getType() == BlockType.AIR) continue;
                                this.blocks[index2].setType(BlockType.DIRT);
                            }
                        }

                        // TODO: Make this happen twice at the same time, 3 times per block rather than 6
                        if (getBlock(i, k, j + 1).transparent) this.blocks[index].showFace(0);
                        if (getBlock(i, k, j - 1).transparent) this.blocks[index].showFace(1);
                        if (getBlock(i + 1, k, j).transparent) this.blocks[index].showFace(2);
                        if (getBlock(i - 1, k, j).transparent) this.blocks[index].showFace(3);
                        if (getBlock(i, k + 1, j).transparent) this.blocks[index].showFace(4);
                        if (getBlock(i, k - 1, j).transparent) this.blocks[index].showFace(5);

                        boolean cont = false;
                        for (int a = 0; a < 6; a++) {
                            if (this.blocks[index].faces[a]) {
                                cont = true;
                                break;
                            }
                        }

                        if (cont) this.meshComponent.combine(this.blocks[index].getMeshComponent());
                    }
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

    BlockType getBlock(int x, int y, int z) {
        if (x == 16) return generateBlock(cx + 1, cz, 0, y, z);
        if (x == -1) return generateBlock(cx - 1, cz, 15, y, z);
        if (z == 16) return generateBlock(cx, cz + 1, x, y, 0);
        if (z == -1) return generateBlock(cx, cz - 1, x, y, 15);

        if (y == -1 || y == 256) return BlockType.AIR;

        return blocks[flatten(x, z, y)].getType();
    }

    boolean isEmpty() {
        return empty;
    }

    static int flatten(int i, int j, int k) {
        return (k << 8) | (i << 4) | j;
    }

    public void cleanup() {
        mesh.cleanup();
    }
}
