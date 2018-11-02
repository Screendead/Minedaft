package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.Mesh;
import com.screendead.minedaft.graphics.MeshComponent;
import org.joml.Vector2i;
import org.joml.Vector3i;

public class Chunk {
    public int cx, cz;
    private Block[] blocks = new Block[65536];

    public Chunk(int cx, int cz) {
        this.cx = cx;
        this.cz = cz;

        for (int k = 0; k < 255; k++) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    int index = flatten(i, j, k);
                    blocks[index] = new Block((k < 4) ? BlockType.GRASS : BlockType.AIR, new Vector3i(i, k, j));
//                    blocks[index] = new Block((k == 0 && j == 0 && i <= 1) ? BlockType.GRASS : BlockType.AIR, new Vector3i(i, k, j));
                }
            }
        }
    }

    public Mesh toMesh() {
        MeshComponent m = new MeshComponent(new float[] {}, new float[] {}, new int[] {});

        for (int k = 0; k < 255; k++) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    int index = flatten(i, j, k);
                    if (blocks[index].getID() == 0) continue;
                    for (int a = 0; a < 6; a++) blocks[index].showFace(a);

//                    if () blocks[index].showFace(0);

                    m.combine(blocks[index].getMeshComponent());
                }
            }
        }

        return m.toMesh();
    }

    private static int flatten(int i, int j, int k) {
        return (k << 8) | (i << 4) | j;
    }
}
