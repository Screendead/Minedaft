package com.screendead.minedaft.world;

import com.screendead.minedaft.performance.ChunkManager;
import org.joml.Matrix4f;

public class World {
    private final ChunkManager chunkManager;

    public World(int renderDistance) {
        this.chunkManager = new ChunkManager(renderDistance);

        chunkManager.generate();
    }

    /**
     * @param cx The X position, in chunk co-ordinates, of the camera.
     * @param cz The Z position, in chunk co-ordinates, of the camera.
     */
    public void update(int cx, int cz) {
//        int poll = chunkManager.poll();
//        if (poll != -1) chunks[poll % xSize][(poll / xSize) % zSize] = chunkManager.get(poll);
        chunkManager.update(cx, cz);
    }

    public void poll(int cx, int cz) {
        chunkManager.poll(cx, cz);
    }

    public void render(float w, float h, Matrix4f view, Matrix4f transform, Matrix4f camera) {
        chunkManager.render(w, h, view, transform, camera);
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
