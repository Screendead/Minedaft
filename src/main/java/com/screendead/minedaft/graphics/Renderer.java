package com.screendead.minedaft.graphics;

import com.screendead.minedaft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class Renderer {
    private Shader shader;
    World world;
    private float width = 0, height = 0;
//    public Vector3f lampPos;
    private float fov = 100.0f;
    private int renderDistance;

    Matrix4f view = new Matrix4f(), transform = new Matrix4f();

    /**
     * Render to the framebuffer
     */
    public void render(Camera camera) {
        // Clear the framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Update the camera in the shader
        shader.bind();
            shader.setUniform("camera", camera.getMatrix());
//            shader.setUniform("viewPos", camera.pos);
//            shader.setUniform("lampPos", lampPos);
        Shader.unbind();

        // Render the chunk mesh
        shader.bind();
            world.render(view, transform, camera.getMatrix());
        Shader.unbind();
    }

    /**
     * Initialise OpenGL context for use with this window
     */
    public void init() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Enable 2D texturing
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glEnable(GL_MULTISAMPLE);

        // OpenGL settings
        glCullFace(GL_BACK);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Create texture and shader
        Mesh.setGlobalTexture(new Image("texture_map.png"));
        shader = new Shader("basic");
        shader.addUniform("view");
        shader.addUniform("transform");
        shader.addUniform("camera");
        shader.addUniform("tex");
//        shader.addUniform("viewPos");
//        shader.addUniform("lampPos");

        this.renderDistance = 4;
        world = new World(this.renderDistance);
//        lampPos = new Vector3f(8 * renderDistance, 128, 8 * renderDistance);

        // Set the sampler2D to 0
        shader.bind();
            shader.setUniform("tex", 0);
        Shader.unbind();

        int[] rgb = new int[] {
                100, 150, 256
        };

        // Set the clear color
        glClearColor(rgb[0] / 255.0f, rgb[1] / 255.0f, rgb[2] / 255.0f, 1.0f);
    }

    /**
     * Set the OpenGL viewport transformation and update the viewMatrix
     * @param width The window width
     * @param height The window height
     */
    public void setViewport(float width, float height) {
        this.width = width;
        this.height = height;

        // Set the viewport
        glViewport(0, 0, (int) width, (int) height);

        // Set the viewMatrix
        view = new Matrix4f()
                .perspective((float) Math.toRadians(fov),
                width / height, 0.01f, renderDistance * (float) Math.sqrt(512));

        // Update the viewMatrix in the shader
        shader.bind();
            shader.setUniform("view", view);
        Shader.unbind();
    }

    /**
     * Set the FOV and update the view matrix accordingly
     * @param fov The field of view in degrees.
     */
    public void setFOV(float fov) {
        this.fov = fov;
    }

    /**
     * Set the transformation matrix for the shader
     * Rotation order is YXZ
     * @param dx X component of the translation
     * @param dy Y component of the translation
     * @param dz Z component of the translation
     * @param rx Degrees of rotation about the X axis
     * @param ry Degrees of rotation about the Y axis
     * @param rz Degrees of rotation about the Z axis
     * @param sx X component of the scale
     * @param sy Y component of the scale
     * @param sz Z component of the scale
     */
    public void setTransform(float dx, float dy, float dz, float rx, float ry, float rz, float sx, float sy, float sz) {
        transform = new Matrix4f()
                .translation(dx, dy, dz)
                .rotateYXZ((float) Math.toRadians(ry), (float) Math.toRadians(rx), (float) Math.toRadians(rz))
                .scale(sx, sy, sz);

        shader.bind();
            shader.setUniform("transform", transform);
        Shader.unbind();
    }

    public void cleanup() {
        world.cleanup();
    }
}
