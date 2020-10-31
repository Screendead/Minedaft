package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.MeshComponent;
import com.screendead.minedaft.performance.ChunkManager;
import org.lwjgl.stb.STBPerlin;

public class World {
    private final ChunkManager chunkManager;

    public World(int renderDistance) {
        this.chunkManager = new ChunkManager(renderDistance);

        chunkManager.generate();
    }

    public void update() {
//        int poll = chunkManager.poll();
//        if (poll != -1) chunks[poll % xSize][(poll / xSize) % zSize] = chunkManager.get(poll);
        chunkManager.poll();
    }

    public void render() {
        for (Chunk c : chunkManager.data) {
            if (c != null) c.render();
        }
    }

//    private BlockType getBlock(int cx, int cz, int x, int y, int z) {
//        if (cx < 0 || cx >= chunks.length || cz < 0 || cz >= chunks[0].length) return BlockType.AIR;
//        if (chunks[cx][cz].isEmpty() || (y < 0) || (y > 255)) return BlockType.AIR;
//        if (x == 16) return getBlock(cx + 1, cz, 0, y, z);
//        if (x == -1) return getBlock(cx - 1, cz, 15, y, z);
//        if (z == 16) return getBlock(cx, cz + 1, x, y, 0);
//        if (z == -1) return getBlock(cx, cz - 1, x, y, 15);
//        return chunks[cx][cz].getBlock(x, y, z);
//    }

    public void cleanup() {
        chunkManager.cleanup();
    }
}
