package com.screendead.minedaft.performance;

import com.screendead.minedaft.graphics.Mesh;
import com.screendead.minedaft.world.Chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ChunkManager {
    int renderDistance;
    private final List<int[]> generated = new ArrayList<>();
    public volatile List<Chunk> data = new ArrayList<>();
    ExecutorService pool;
    List<int[]> locations = new ArrayList<>();
    List<Future<Chunk>> futures = new ArrayList<>();

    public ChunkManager(int renderDistance) {
        this.renderDistance = renderDistance;

        pool = Executors.newScheduledThreadPool(0);
//        pool = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() / 2 - 1);
//        pool = Executors.newFixedThreadPool(xSize * zSize);
    }

    public void generate() {
        smartGenAroundPlayer(new int[]{0, 0});
    }

    public void poll(int cx, int cz) {
        smartGenAroundPlayer(new int[]{cx, cz});

        if (futures.size() == 0) return;

        for (int i = futures.size() - 1; i >= 0; i--) {
            if (!futures.get(i).isDone()) continue;

            try {
                Chunk c = futures.get(i).get();
                if (!generated.contains(new int[]{c.cx, c.cz})) {
                    data.add(c);
                    generated.add(new int[]{c.cx, c.cz});
                    locations.remove(indexOf(locations, new int[]{c.cx, c.cz}));
                }
                futures.remove(i);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        for (int i = data.size() - 1; i >= 0; i--) {
            if (data.get(i).cx > renderDistance + cx
                    || data.get(i).cx < -renderDistance + cx
                    || data.get(i).cz > renderDistance + cz
                    || data.get(i).cz < -renderDistance + cz) {
                generated.remove(indexOf(generated, new int[]{data.get(i).cx, data.get(i).cz}));
                data.remove(i);
            }
        }
    }

    private void smartGenAroundPlayer(int[] cxcz) {
        int cx = cxcz[0];
        int cz = cxcz[1];

        smartAddChunkToQueue(cxcz);
        for (int i = 0; i < renderDistance; i++) {
            for (int x = i; x > -i; x--) {
                if (Math.abs(x) == i) {
                    smartAddChunkToQueue(new int[]{cx + x, cz});
                    smartAddChunkToQueue(new int[]{cx - x, cz});
                } else {
                    smartAddChunkToQueue(new int[]{cx + x, cz + i - Math.abs(x)});
                    smartAddChunkToQueue(new int[]{cx + x, cz - (i - Math.abs(x))});
                }
            }
        }
    }

    private void smartAddChunkToQueue(int[] cxcz) {
        if (contains(locations, cxcz)) return;
        locations.add(cxcz);

        futures.add(pool.submit(() -> Chunk.generate(cxcz[0], cxcz[1])));
    }

    private static int indexOf(List<int[]> list, int[] item) {
        for (int i = 0; i < list.size(); i++) {
            if (Arrays.equals(list.get(i), item)) return i;
        }
        return -1;
    }

    private static boolean contains(List<int[]> list, int[] item) {
        for (int[] ints : list) {
            if (Arrays.equals(ints, item)) return true;
        }
        return false;
    }

    public void cleanup() {
        pool.shutdownNow();

        for (Chunk c : data) c.cleanup();
    }
}
