package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.Mesh;
import com.screendead.minedaft.graphics.MeshComponent;
import org.joml.Vector3i;
import org.lwjgl.stb.STBPerlin;

public class Chunk {
    public int cx, cz;
    private Block[] blocks = new Block[65536];
    private Mesh mesh = null;

    public Chunk(int cx, int cz) {
        this.cx = cx;
        this.cz = cz;

        float scale = 0.02f;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                float x = (float) (i + (cx << 4)) * scale, z = (float) (j + (cz << 4)) * scale;
                float height = 64 + 16 * STBPerlin.stb_perlin_noise3(x, 0, z, 0, 0, 0);
                for (int k = 0; k < 256; k++) {
                    int index = flatten(i, j, k);
                    blocks[index] = new Block((k < height) ? BlockType.GRASS : BlockType.AIR, new Vector3i(i + (cx << 4), k, j + (cz << 4)));
//                    blocks[index] = new Block(BlockType.GRASS, new Vector3i(i + (cx << 4), k, j + (cz << 4)));
                }
            }
        }
    }

    public void render() {
        if (mesh == null) generateMesh();

        mesh.render();
    }

    public void generateMesh() {
        MeshComponent m = new MeshComponent(new float[] {}, new float[] {}, new int[] {});

        for (int k = 0; k < 256; k++) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    if (getBlock(i, k, j) == BlockType.AIR.getID()) continue;

                    int index = flatten(i, j, k);

//                    for (int a = 0; a < 6; a++) blocks[index].showFace(a);

                    if (getBlock(i, k, j + 1) == BlockType.AIR.getID()) blocks[index].showFace(0);
                    if (getBlock(i, k, j - 1) == BlockType.AIR.getID()) blocks[index].showFace(1);
                    if (getBlock(i + 1, k, j) == BlockType.AIR.getID()) blocks[index].showFace(2);
                    if (getBlock(i - 1, k, j) == BlockType.AIR.getID()) blocks[index].showFace(3);
                    if (getBlock(i, k + 1, j) == BlockType.AIR.getID()) blocks[index].showFace(4);
                    if (getBlock(i, k - 1, j) == BlockType.AIR.getID()) blocks[index].showFace(5);

                    boolean cont = false;
                    for (int a = 0; a < 6; a++) {
                        if (blocks[index].faces[a]) {
                            cont = true;
                            break;
                        }
                    }

                    if (cont) m.combine(blocks[index].getMeshComponent());
                }
            }
        }

        mesh = m.toMesh();
    }

    private int getBlock(int x, int y, int z) {
        if ((x < 0) || (x > 15) || (y < 0) || (y > 255) || (z < 0) || (z > 15)) return BlockType.AIR.getID();
        return blocks[flatten(x, z, y)].getID();
    }

    private static int flatten(int i, int j, int k) {
        return (k << 8) | (i << 4) | j;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void cleanup() {
        mesh.cleanup();
    }
}
