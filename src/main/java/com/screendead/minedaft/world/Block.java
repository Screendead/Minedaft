package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.MeshComponent;
import org.joml.Vector3i;

public class Block {
    boolean[] faces = new boolean[] {
            false, // +Z
            false, // -Z
            false, // +X
            false, // -X
            false, // +Y
            false // -Y
    };

    private boolean shaded = false;

    private BlockType type;
    private Vector3i position;

    Block(BlockType type, Vector3i position) {
        this.type = type;
        this.position = position;
    }

    void showFace(int index) {
        faces[index] = true;
    }

    public void hideFace(int index) {
        faces[index] = false;
    }

    void setShaded(boolean shaded) {
        this.shaded = shaded;
    }

    void setType(BlockType type) {
        this.type = type;
    }

    MeshComponent getMeshComponent() {
        return type.getMeshComponent(faces, position.x, position.y, position.z, shaded);
    }

    BlockType getType() {
        return type;
    }

    int getID() {
        return type.getID();
    }

    public String getName() {
        return type.getName();
    }
}
