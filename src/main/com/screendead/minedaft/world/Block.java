package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.MeshComponent;

public class Block {
    public boolean[] faces = new boolean[] {
            false, // +Z
            false, // -Z
            false, // +X
            false, // -X
            false, // +Y
            false // -Y
    };

    private BlockType type;

    public Block(BlockType type) {
        this.type = type;
    }

    public void showFace(int index) {
        faces[index] = true;
    }

    public void hideFace(int index) {
        faces[index] = false;
    }

    public MeshComponent getMeshComponent() {
        return type.getMeshComponent(faces);
    }

    public int getID() {
        return type.getID();
    }

    public String getName() {
        return type.getName();
    }
}
