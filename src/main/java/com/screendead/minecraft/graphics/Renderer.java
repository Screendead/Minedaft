package com.screendead.minecraft.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class Renderer {
    private Image texture;
    private Shader shader;
    private Mesh mesh;
    private Matrix4f view, transform;

    public Renderer() {
        view = new Matrix4f();
    }

    /**
     * Render to the framebuffer
     */
    public void render() {
        // Clear the framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Bind the shader
        shader.bind();
            // Render the mesh
            mesh.render();
        // Unbind the shader
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
//        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glEnable(GL_MULTISAMPLE);

        // OpenGL settings
//        glCullFace(GL_FRONT);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Create texture and shader
        texture = new Image("grassblock.png");
        shader = new Shader("basic");
        shader.addUniform("view");
        shader.addUniform("transform");
        shader.addUniform("tex");

        // Create values for mesh generation
        float[] positions = {
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,

                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f,
        };
        float[] texCoords = {
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,

                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,

                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,

                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = {
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7,
        };

        // Create a mesh
        mesh = new Mesh(positions, texCoords, indices, texture);
//        mesh = new Mesh(new float[] {
//                // VO
//                -0.5f,  0.5f,  0.5f,
//                // V1
//                -0.5f, -0.5f,  0.5f,
//                // V2
//                 0.5f, -0.5f,  0.5f,
//                // V3
//                 0.5f,  0.5f,  0.5f,
//                // V4
//                -0.5f,  0.5f, -0.5f,
//                // V5
//                 0.5f,  0.5f, -0.5f,
//                // V6
//                -0.5f, -0.5f, -0.5f,
//                // V7
//                 0.5f, -0.5f, -0.5f
//        }, new float[] {
//                0.0f, 0.0f, // 0 Top left
//                0.0f, 1.0f, // 1 Bottom left
//                1.0f, 0.0f, // 3 Top right
//                1.0f, 0.0f, // 3 Top right
//                0.0f, 1.0f, // 1 Bottom left
//                1.0f, 1.0f, // 2 Bottom right
//        }, new int[] {
//                // Front face
//                0, 1, 3, 3, 1, 2,
//                // Top Face
//                4, 0, 3, 5, 4, 3,
//                // Right face
//                3, 2, 5, 5, 2, 7,
//                // Left face
//                4, 6, 0, 0, 6, 1,
//                // Bottom face
//                1, 6, 2, 2, 6, 7,
//                // Back face
//                5, 7, 4, 4, 7, 6,
//        }, texture);

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
        // Set the viewport
        glViewport(0, 0, (int) width, (int) height);

        // Set the viewMatrix
        view = new Matrix4f().perspective((float) Math.toRadians(70),
                 width / height, 0.1f, 1000.0f)
                .lookAt(new Vector3f(1, 1, 1), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));

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
