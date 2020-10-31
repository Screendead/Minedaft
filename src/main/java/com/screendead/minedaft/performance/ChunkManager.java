package com.screendead.minedaft.performance;

import com.screendead.minedaft.world.Chunk;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ChunkManager {
    int renderDistance;
//    ThreadedChunkGenerator[] generators;
//    Thread[] threads;
    public volatile List<Chunk> data;
    ExecutorService pool;
    List<Future<Chunk>> futures = new ArrayList<>();

    public ChunkManager(int renderDistance) {
        this.renderDistance = renderDistance;

        data = new ArrayList<>();
        pool = Executors.newScheduledThreadPool(0);
//        pool = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() / 2 - 1);
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
        List<int[]> locations = new ArrayList<>();

        locations.add(new int[]{0, 0});
        for (int i = 0; i < renderDistance; i++) {
            for (int x = i; x > -i; x--) {
                if (Math.abs(x) == i) {
                    locations.add(new int[]{x, 0});
                    locations.add(new int[]{-x, 0});
                } else {
                    locations.add(new int[]{x, i - Math.abs(x)});
                    locations.add(new int[]{x, -(i - Math.abs(x))});
                }
            }
        }

        locations.forEach(loc -> {
            final int finalX = loc[0];
            final int finalZ = loc[1];
            futures.add(pool.submit(() -> Chunk.generate(finalX, finalZ)));
        });
    }

    public void poll() {
        if (futures.size() == 0) return;

        for (int i = futures.size() - 1; i >= 0; i--) {
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
