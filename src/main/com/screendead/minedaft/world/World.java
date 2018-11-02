package com.screendead.minedaft.world;

public class World {
    private Chunk[][] chunks;

    public World(int xSize, int zSize) {
        chunks = new Chunk[xSize][zSize];

        for (int x = 0; x < zSize; x++) {
            for (int z = 0; z < xSize; z++) {
                chunks[x][z] = new Chunk(x, z);
            }
        }
    }

    public void render() {
        for (Chunk[] zChunks : chunks) {
            for (Chunk chunk : zChunks) {
                chunk.render();
            }
        }
    }

    public void cleanup() {
        for (Chunk[] zChunks : chunks) {
            for (Chunk chunk : zChunks) {
                chunk.cleanup();
            }
        }
    }
}
