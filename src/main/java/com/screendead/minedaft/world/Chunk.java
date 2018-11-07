package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.Mesh;
import org.joml.Vector3i;
import org.lwjgl.stb.STBPerlin;

public class Chunk {
    public static final Chunk EMPTY = new Chunk();

    private float threshold = 160.0f;

    public int cx, cz;
    Block[] blocks = new Block[65536];
    int[] maxHeight = new int[256];
    Mesh mesh = null;
    private boolean empty = false;

    public Chunk(int cx, int cz) {
        this.cx = cx;
        this.cz = cz;

        float scale = 0.02f;
        float dScale = 0.04f;

        for (int i = 0; i < maxHeight.length; i++) maxHeight[i] = 0;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                float x = (float) (i + (cx << 4)) * scale, z = (float) (j + (cz << 4)) * scale;
                float height = 128.0f * (STBPerlin.stb_perlin_noise3(x, 0, z, 0, 0, 0) - 0.5f);
                for (int k = 0; k < 256; k++) {
                    int index = flatten(i, j, k);
//                    float detail = 48.0f * (STBPerlin.stb_perlin_noise3(x, k * dScale, z, 0, 0, 0) - 1);
                    float detail = 64.0f * (STBPerlin.stb_perlin_turbulence_noise3(x / 3, k * dScale, z / 3, 1.2f, 0.35f, 3, 0, 0, 0) - 0.5f);

                    BlockType type;

                    if (k == 0) type = BlockType.BEDROCK;
                    else if (k <= threshold + height + detail) type = BlockType.STONE;
                    else type = BlockType.AIR;

                    blocks[index] = new Block(type, new Vector3i(i + (cx << 4), k, j + (cz << 4)));
                }
            }
        }
    }

    private Chunk() {
        this.empty = true;
    }

    public void render() {
        if (mesh != null) mesh.render();
    }

//    public void generateMesh(World w) {
//        this.world = w;
//
//        MeshComponent m = new MeshComponent(new float[] {}, new float[] {}, new float[] {}, new int[] {});
//
//        for (int k = 255; k >= 0; k--) {
//            for (int i = 0; i < 16; i++) {
//                for (int j = 0; j < 16; j++) {
//                    if (getBlock(i, k, j) == BlockType.AIR.getID()) continue;
//                    int mh = flatten(i, j, 0);
//                    if (maxHeight[mh] < k) maxHeight[mh] = k;
//
//                    int index = flatten(i, j, k);
//
//                    blocks[index].setShaded(k < maxHeight[mh]);
//                    if ((blocks[index].getType() != BlockType.BEDROCK) && getBlock(i, k + 1, j) == BlockType.AIR.getID())
//                        blocks[index].setType(BlockType.GRASS);
//                    if ((blocks[index].getType() != BlockType.BEDROCK) &&
//                            (getBlock(i, k + 1, j) == BlockType.GRASS.getID() ||
//                            getBlock(i, k + 2, j) == BlockType.GRASS.getID() ||
//                            getBlock(i, k + 3, j) == BlockType.GRASS.getID() ||
//                            getBlock(i, k + 4, j) == BlockType.GRASS.getID()))
//                        blocks[index].setType(BlockType.DIRT);
//
//                    // TODO: Make this happen twice at the same time, 3 times per block rather than 6
//                    if (getBlock(i, k, j + 1) == BlockType.AIR.getID()) blocks[index].showFace(0);
//                    if (getBlock(i, k, j - 1) == BlockType.AIR.getID()) blocks[index].showFace(1);
//                    if (getBlock(i + 1, k, j) == BlockType.AIR.getID()) blocks[index].showFace(2);
//                    if (getBlock(i - 1, k, j) == BlockType.AIR.getID()) blocks[index].showFace(3);
//                    if (getBlock(i, k + 1, j) == BlockType.AIR.getID()) blocks[index].showFace(4);
//                    if (getBlock(i, k - 1, j) == BlockType.AIR.getID()) blocks[index].showFace(5);
//
//                    boolean cont = false;
//                    for (int a = 0; a < 6; a++) {
//                        if (blocks[index].faces[a]) {
//                            cont = true;
//                            break;
//                        }
//                    }
//
//                    if (cont) m.combine(blocks[index].getMeshComponent());
//                }
//            }
//        }
//
//        mesh = m.toMesh();
//    }

    int getBlock(int x, int y, int z) {
        return blocks[flatten(x, z, y)].getID();
    }

    boolean isEmpty() {
        return empty;
    }

    static int flatten(int i, int j, int k) {
        return (k << 8) | (i << 4) | j;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void cleanup() {
        mesh.cleanup();
    }
}
