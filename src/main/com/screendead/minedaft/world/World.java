package com.screendead.minedaft.world;

public class World {
    private Chunk[][] chunks;
    private boolean meshesGenerated = false;

    public World(int xSize, int zSize) {
        chunks = new Chunk[xSize][zSize];

        for (int z = 0; z < chunks.length; z++) {
            for (int x = 0; x < chunks[0].length; x++) {
                chunks[x][z] = new Chunk(x, z);
            }
        }
    }

    public void render() {
        if (!meshesGenerated) generateMeshes();

        for (int z = 0; z < chunks.length; z++) {
            for (int x = 0; x < chunks[0].length; x++) {
                chunks[x][z].render();
            }
        }
    }

    private void generateMeshes() {
        for (int z = 0; z < chunks.length; z++) {
            for (int x = 0; x < chunks[0].length; x++) {
                chunks[x][z].generateMesh(getChunk(x + 1, z), getChunk(x - 1, z), getChunk(x, z + 1), getChunk(x, z - 1));
            }
        }

        meshesGenerated = true;
    }

    private Chunk getChunk(int x, int z) {
        if ((x < 0) || (x >= chunks[0].length) || (z < 0) || (z >= chunks.length)) return Chunk.EMPTY;
        return chunks[x][z];
    }

    public void cleanup() {
        for (int z = 0; z < chunks.length; z++) {
            for (int x = 0; x < chunks[0].length; x++) {
                chunks[x][z].cleanup();
            }
        }
    }
}
