package com.screendead.minedaft.performance;

import com.screendead.minedaft.world.Block;
import com.screendead.minedaft.world.Chunk;

public class ThreadedChunkGenerator implements Runnable {
    int cx, cz;
    private volatile Block[] data;

    public ThreadedChunkGenerator(int cx, int cz) {
        this.cx = cx;
        this.cz = cz;
    }

    @Override
    public void run() {
//        this.data = Chunk.generate(cx, cz);
    }

    public Block[] get() {
        return data;
    }
}
