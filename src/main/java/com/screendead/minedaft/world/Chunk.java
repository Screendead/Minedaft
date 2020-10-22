package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.Mesh;
import org.joml.Vector3i;
import org.lwjgl.stb.STBPerlin;

import java.util.Arrays;

public class Chunk {
    public static final Chunk EMPTY = new Chunk();

    private float threshold = 128.0f;

    public int cx, cz;
    Block[] blocks = new Block[65536];
    int[] maxHeight = new int[256];
    Mesh mesh = null;
    private boolean empty = false;

    public Chunk(int cx, int cz) {
        this.cx = cx;
        this.cz = cz;

        float scale = 0.02f;
        float dScale = 0.02f;

        Arrays.fill(maxHeight, 0);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                float x = (float) (i + (cx << 4)) * scale, z = (float) (j + (cz << 4)) * scale;
                float height = 64.0f * (STBPerlin.stb_perlin_noise3(x, 0, z, 0, 0, 0) - 0.5f);
                for (int k = 0; k < 256; k++) {
                    int index = flatten(i, j, k);
                    float detail = 64.0f * (STBPerlin.stb_perlin_noise3(x, k * dScale, z, 0, 0, 0) - 0.5f);
//                    float detail = 128.0f * (STBPerlin.stb_perlin_turbulence_noise3(x, k * dScale, z, 1.2f, 0.35f, 3, 0, 0, 0) - 0.5f);

                    BlockType type;

                    if (k == 0) type = BlockType.BEDROCK;
                    else if (k <= threshold + height + detail) type = BlockType.STONE;
//                    else if (k <= threshold + height) type = BlockType.STONE;
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
