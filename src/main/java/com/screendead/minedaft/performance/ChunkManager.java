package com.screendead.minedaft.performance;

import com.screendead.minedaft.world.Chunk;
import org.joml.Matrix4f;
import org.joml.Vector3i;

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

    private final Vector3i camPos = new Vector3i();
    private final Matrix4f mx = new Matrix4f();

    public ChunkManager(int renderDistance) {
        this.renderDistance = renderDistance;

//        pool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() / 2 - 2);
//        pool = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() - 4);
        pool = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() - 1);
//        pool = Executors.newFixedThreadPool(64);
    }

    public void generate() {
        smartGenAroundPlayer(0, 0);
    }

    public void update(int cx, int cz) {
        camPos.x = cx;
        camPos.z = cz;
        smartGenAroundPlayer(cx, cz);
    }

    public void poll(int cx, int cz) {
        if (futures.size() == 0) return;

        for (int i = locations.size() - 1; i >= 0; i--) {
            if (testCircle(renderDistance * 2, locations.get(i)[0], locations.get(i)[1])
                || testAABB(locations.get(i)[0], 128, locations.get(i)[1])) {
                futures.get(i).cancel(true);
                futures.remove(i);
                locations.remove(i);
            }
        }

        for (int i = futures.size() - 1; i >= 0; i--) {
            if (!futures.get(i).isDone()) continue;

            try {
                Chunk c = futures.get(i).get();
                if (!contains(generated, c.cx, c.cz)) {
                    data.add(c);
                    generated.add(new int[]{c.cx, c.cz});
                    locations.remove(indexOf(locations, c.cx, c.cz));
                }
                futures.remove(i);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        for (int i = data.size() - 1; i >= 0; i--) {
            if (testCircle(renderDistance * 2, data.get(i).cx, data.get(i).cz)) {
                generated.remove(indexOf(generated, data.get(i).cx, data.get(i).cz));
                data.remove(i);
            }
        }
    }

    public void render(Matrix4f view, Matrix4f transform, Matrix4f camera) {
        this.mx.set(view)
                .mul(transform)
                .mul(camera);

//        this.data.forEach(Chunk::render);

        this.data.forEach(chunk -> {
            for (int y = 0; y < 16; y++) {
                if (testAABB(chunk.cx, y, chunk.cz)) {
                    chunk.render(y);
                }
            }
//
////            Vector4f xz = new Vector4f(chunk.cx * 16 + 8, 128, chunk.cz * 16 + 8, 1);
////            xz.mul(mx);
////            if (mx.testPoint(xz.x, xz.y, xz.z)) {
////                chunk.render();
////            }
        });
    }

    private void smartGenAroundPlayer(int cx, int cz) {
        smartAddChunkToQueue(cx, cz);
        for (int i = 0; i <= renderDistance * 2; i++) {
            for (int x = i; x > -i; x--) {
                if (Math.abs(x) == i) {
                    smartAddChunkToQueue(cx + x, cz);
                    smartAddChunkToQueue(cx - x, cz);
                } else {
                    smartAddChunkToQueue(cx + x, cz + i - Math.abs(x));
                    smartAddChunkToQueue(cx + x, cz - (i - Math.abs(x)));
                }
            }
        }
    }

    private void smartAddChunkToQueue(int cx, int cz) {
        if (testCircle(renderDistance, cx, cz)) return;
        if (contains(locations, cx, cz) || contains(generated, cx, cz)) return;

        locations.add(new int[]{cx, cz});
        futures.add(pool.submit(() -> Chunk.generate(cx, cz)));
    }

    private boolean testCircle(float d, int cx, int cz) {
        int x = cx - camPos.x;
        int z = cz - camPos.z;
        return x*x + z*z >= d*d;
    }

    private boolean testAABB(int cx, int cy, int cz) {
        return mx.testAab(cx << 4, cy << 4, cz << 4, (cx + 1) << 4, (cy + 1) << 4, (cz + 1) << 4);
//        return mx.testPoint((cx << 4) + 8, (cy << 4) + 8, (cz << 4) + 8);
    }

    private static int indexOf(List<int[]> list, int cx, int cz) {
        for (int i = 0; i < list.size(); i++) {
            if (Arrays.equals(list.get(i), new int[]{cx, cz})) return i;
        }
        return -1;
    }

    private static boolean contains(List<int[]> list, int cx, int cz) {
        for (int[] ints : list) {
            if (Arrays.equals(ints, new int[]{cx, cz})) return true;
        }
        return false;
    }

    public void cleanup() {
        pool.shutdownNow();

        for (Chunk c : data) c.cleanup();
    }
}
