package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.Mesh;
import org.joml.Random;
import org.joml.Vector3i;
import org.lwjgl.stb.STBPerlin;

public class Chunk {
    public static final Chunk EMPTY = new Chunk();
    private static final int SEED = new Random(0).nextInt(Integer.MAX_VALUE);

    private float threshold = 32.0f;

    private int cx, cz;
    Block[] blocks = new Block[65536];
    int[] maxHeight = new int[256];
    Mesh mesh = null;
    private boolean empty = false;

    Chunk(int cx, int cz) {
        this.cx = cx + SEED;
        this.cz = cz + SEED;

        float scale = 0.015f;
        float dScale = 0.02f;

        for (int i = 0; i < maxHeight.length; i++) maxHeight[i] = 0;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                float x = (float) (i + (cx << 4)) * scale, z = (float) (j + (cz << 4)) * scale;
//                float height = 64.0f * (STBPerlin.stb_perlin_noise3(x, 0, z, 0, 0, 0) - 0.5f);
                double dist = 8 * (Math.sqrt(Math.abs(Math.pow(x, 2) + Math.pow(z, 2))) + 1);
                float height = 8.0f * ((float) Math.sin(dist));
                for (int k = 0; k < 256; k++) {
                    int index = flatten(i, j, k);
//                    float detail = 64.0f * (STBPerlin.stb_perlin_noise3(x, k * dScale, z, 0, 0, 0) - 0.5f);
                    float detail = 16.0f * (STBPerlin.stb_perlin_noise3(x, k * dScale, z, 0, 0, 0) - 0.5f);
                    detail += 8.0f * ((float) Math.cos(dist));
//                    float detail = 128.0f * (STBPerlin.stb_perlin_turbulence_noise3(x, k * dScale, z, 1.2f, 0.35f, 3, 0, 0, 0) - 0.5f);

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

    void render() {
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

    void cleanup() {
        mesh.cleanup();
    }
}
