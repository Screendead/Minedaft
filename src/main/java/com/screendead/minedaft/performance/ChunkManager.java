package com.screendead.minedaft.performance;

import com.screendead.minedaft.world.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ChunkManager {
    int xSize, zSize;
//    ThreadedChunkGenerator[] generators;
//    Thread[] threads;
    public volatile List<Chunk> data;
    ExecutorService pool;
    List<Future<Chunk>> futures = new ArrayList<>();

    public ChunkManager(int xSize, int zSize) {
        this.xSize = xSize;
        this.zSize = zSize;

        data = new ArrayList<>();
//        pool = Executors.newScheduledThreadPool(0);
        pool = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() - 2);
//        pool = Executors.newFixedThreadPool(xSize * zSize);

//        generators = new ThreadedChunkGenerator[xSize * zSize];
//        threads = new Thread[xSize * zSize];
//        for (int i = 0; i < generators.length; i++) {
//            int cx = i % xSize;
//            int cz = (i / xSize) % zSize;
//
//            generators[i] = new ThreadedChunkGenerator(cx, cz);
//
//            threads[i] = new Thread(generators[i], String.valueOf(i));
//        }
    }

    public void generate() {
        for (int x = 0; x < xSize; x++) {
            for (int z = 0; z < zSize; z++) {
                int finalX = x;
                int finalZ = z;
                futures.add(pool.submit(() -> Chunk.generate(finalX, finalZ)));
            }
        }
    }

    public void poll() {
        if (futures.size() == 0) return;

        for (int i = futures.size() - 1; i >= 0; i--) {
            int x = i % xSize;
            int z = (i / xSize) % zSize;

//            if (data[x][z] != null) continue;
            if (!futures.get(i).isDone()) continue;

            try {
                data.add(futures.get(i).get());
                futures.remove(i);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public void cleanup() {
        pool.shutdownNow();

        for (Chunk c : data) {
            if (c != null) c.cleanup();
        }
    }
}
