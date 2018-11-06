package com.screendead.minedaft.graphics;

import com.screendead.minedaft.Minedaft;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.FloatBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int program;
    private int vert;
    private int frag;

    private HashMap<String, Integer> uniforms;

    Shader(String filename) {
        uniforms = new HashMap<>();

        // Create the shader program on the graphics card
        program = glCreateProgram();

        // Allocate the vertex shader
        vert = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vert, readFile(filename + ".vert"));
        glCompileShader(vert);

        // Compile the vertex shader
        if (glGetShaderi(vert, GL_COMPILE_STATUS) != 1) throw new RuntimeException(glGetShaderInfoLog(vert));

        // Allocate the fragment shader
        frag = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(frag, readFile(filename + ".frag"));
        glCompileShader(frag);

        // Compile the fragment shader
        if (glGetShaderi(frag, GL_COMPILE_STATUS) != 1) throw new RuntimeException(glGetShaderInfoLog(frag));

        // Attach the vertex and fragment shaders to the shader program
        glAttachShader(program, vert);
        glAttachShader(program, frag);

        // Set locations for data to be sent to the vertex shader
        glBindAttribLocation(program, 0, "position");
        glBindAttribLocation(program, 1, "normals");
        glBindAttribLocation(program, 2, "textures");

        // Link and validate the shaders
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) != 1) throw new RuntimeException(glGetProgramInfoLog(program));
        glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) != 1) throw new RuntimeException(glGetProgramInfoLog(program));
    }

    /**
     * Use this shader
     */
    void bind() {
        glUseProgram(program);
    }

    /**
     * Unbind all shaders
     */
    static void unbind() {
        glUseProgram(0);
    }

    /**
     * Create a writable variable for use in the shaders
     * @param name The exact name of the variable
     */
    void addUniform(String name) {
        int uniformLocation = glGetUniformLocation(program, name);
        if (uniformLocation == 0xFFFFFFFF) throw new RuntimeException("Failed to find uniform: " + name);

        uniforms.put(name, uniformLocation);
    }

    /**
     * Assign a boolean value to a uniform variable
     * @param name The name of the uniform variable
     * @param value The value to assign
     */
    public void setUniform(String name, boolean value) {
        glUniform1i(uniforms.get(name), (value) ? 1 : 0);
    }

    /**
     * Assign an integer value to a uniform variable
     * @param name The name of the uniform variable
     * @param value The value to assign
     */
    void setUniform(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }

    /**
     * Assign a floating-point value to a uniform variable
     * @param name The name of the uniform variable
     * @param value The value to assign
     */
    public void setUniform(String name, float value) {
        glUniform1f(uniforms.get(name), value);
    }

    /**
     * Assign a 2D floating-point vector to a uniform variable
     * @param name The name of the uniform variable
     * @param value The value to assign
     */
    public void setUniform(String name, Vector2f value) {
        glUniform2f(uniforms.get(name), value.x, value.y);
    }

    /**
     * Assign a 3D floating-point vector to a uniform variable
     * @param name The name of the uniform variable
     * @param value The value to assign
     */
    public void setUniform(String name, Vector3f value) {
        glUniform3f(uniforms.get(name), value.x, value.y, value.z);
    }

    /**
     * Assign a 4x4 floating-point matrix to a uniform variable
     * @param name The name of the uniform variable
     * @param value The value to assign
     */
    void setUniform(String name, Matrix4f value) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        value.get(buffer);

        glUniformMatrix4fv(uniforms.get(name), false, buffer);
    }

    /**
     * Parse a shader string from a file
     * @param filename The name of the file (without a path)
     * @return A parsed string representing the shader in the file
     */
    private static String readFile(String filename) {
        StringBuilder source = new StringBuilder();
        BufferedReader reader;

        try {
//            reader = new BufferedReader(new FileReader("C:/Users/admin/Documents/IntelliJ IDEA Projects/Minedaft/res/shaders/" + filename));
            reader = new BufferedReader(new FileReader(Minedaft.getResource("shaders/" + filename)));

            String line;
            while ((line = reader.readLine()) != null) {
                source.append(line);
                source.append("\n");
            }
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getClass() + ": " + e.getMessage());
        }

        return source.toString();
    }
}
