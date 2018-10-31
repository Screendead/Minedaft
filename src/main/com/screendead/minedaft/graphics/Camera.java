package com.screendead.minedaft.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    public Vector3f up = new Vector3f(0, 1, 0);
    public Vector3f pos, look;
    public float horizontal = 0, vertical = 0;
    private Matrix4f lookMatrix;

    public Camera() {
        this(new Vector3f(0, 1, 0));
    }

    public Camera(Vector3f pos) {
        this(pos, new Vector3f(0, -1, -1));
    }

    public Camera(Vector3f pos, Vector3f look) {
        this.pos = pos;
        this.look = look.normalize();
        update(0, 0);
    }

    public void update(float dx, float dy) {
        horizontal += -dx / 12.0f;
        vertical += (dy * 2) / 12.0f;

        horizontal = horizontal % 360.0f;
        vertical = constrain(vertical, -89.99f, 89.99f);

        this.look = new Vector3f(0, 0, -1).rotateAxis((float) Math.toRadians(horizontal), up.x, up.y, up.z);
        Vector3f right = up.cross(look, new Vector3f()).normalize();
        this.look.rotateAxis((float) Math.toRadians(vertical), right.x, right.y, right.z);
        lookMatrix = new Matrix4f().lookAt(this.pos, this.pos.add(this.look, new Vector3f()), this.up);
    }

    private float constrain(float f, float min, float max) {
        return Math.min(Math.max(f, min), max);
    }

    public Matrix4f getMatrix() {
        return lookMatrix;
    }
}
