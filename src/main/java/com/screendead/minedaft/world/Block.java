package com.screendead.minedaft.world;

import com.screendead.minedaft.graphics.MeshComponent;
import org.joml.Vector3i;

public class Block {
    public boolean[] faces = new boolean[] {
            false, // +Z
            false, // -Z
            false, // +X
            false, // -X
            false, // +Y
            false // -Y
    };

    public boolean shaded = false;

    private BlockType type;
    private Vector3i position;

    public Block(BlockType type, Vector3i position) {
        this.type = type;
        this.position = position;
    }

    public void showFace(int index) {
        faces[index] = true;
    }

    public void hideFace(int index) {
        faces[index] = false;
    }

    public void setShaded(boolean shaded) {
        this.shaded = shaded;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    public MeshComponent getMeshComponent() {
        return type.getMeshComponent(faces, position.x, position.y, position.z, shaded);
    }

    public BlockType getType() {
        return type;
    }

    public int getID() {
        return type.getID();
    }

    public String getName() {
        return type.getName();
    }
}
