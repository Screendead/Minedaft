package com.screendead.minedaft.graphics;

import com.screendead.minedaft.world.Block;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;

import static com.screendead.minedaft.world.BlockType.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class Renderer {
    private Image texture;
    private Shader shader;
    private Block block;
    private Mesh mesh;
    private Matrix4f view;
    private int width, height;

    public Renderer() {
        view = new Matrix4f();
    }

    /**
     * Render to the framebuffer
     */
    public void render(Camera camera) {
        // Clear the framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Update the camera in the shader
        shader.bind();
            shader.setUniform("camera", camera.getMatrix());
        Shader.unbind();

//        for (int i = 0; i < 16; i++) {
//            for (int j = 0; j < 16; j++) {
//                for (int k = -16; k < 0; k++) {
//                    setTransform(i, k, j, 0, 0, 0, 1, 1, 1);
                    // Bind the shader
                    shader.bind();
                        // Render the mesh
                        mesh.render();
                    // Unbind the shader
                    Shader.unbind();
//                }
//            }
//        }
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
        Mesh.setGlobalTexture(new Image("grassblock.png"));
        shader = new Shader("basic");
        shader.addUniform("view");
        shader.addUniform("transform");
        shader.addUniform("camera");
        shader.addUniform("tex");

        block = new Block(GRASS);
        for (int i = 0; i < 6; i++) block.showFace(i);
        mesh = block.getMeshComponent().toMesh();

        // Set the sampler2D to 0
        shader.bind();
            shader.setUniform("tex", 0);
        Shader.unbind();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Set the OpenGL viewport transformation and update the viewMatrix
     * @param width The window width
     * @param height The window height
     */
    public void setViewport(float width, float height) {
        this.width = (int) width;
        this.height = (int) height;

        // Set the viewport
        glViewport(0, 0, (int) width, (int) height);

        // Set the viewMatrix
        view = new Matrix4f();
        view.perspective((float) Math.toRadians(100.0f),
                width / height, 0.1f, 1000.0f);

        // Update the viewMatrix in the shader
        shader.bind();
            shader.setUniform("view", view);
        Shader.unbind();
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
        shader.bind();
            shader.setUniform("transform", new Matrix4f().translation(dx, dy, dz)
                    .rotateYXZ((float) Math.toRadians(ry), (float) Math.toRadians(rx), (float) Math.toRadians(rz))
                    .scale(sx, sy, sz));
        Shader.unbind();
    }
}
