package com.screendead.minedaft;

import com.screendead.minedaft.graphics.Window;
import org.joml.Vector2i;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    public boolean[] keys = new boolean[68836];
    public boolean[] mods = new boolean[68836];

    public double x, y;
    public float dx, dy;
    private boolean firstMouse = true;
    private int mm = 0;

    private GLFWWindowSizeCallback windowSizeCallback;
    private GLFWCursorPosCallback cursorPosCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;
    private GLFWScrollCallback scrollCallback;
    private GLFWWindowFocusCallback windowFocusCallback;

    private Window window;

    public Input(Window window) {
        this.window = window;

        Vector2i size = window.getSize();
        x = (float) size.x / 2.0f;
        y = (float) size.y / 2.0f;

        init();
    }

    private void onWindowSizeChanged(int width, int height) {
        window.setSize(width, height);
    }

    private void onFocusChanged(boolean focused) {

    }

    private void onKeyPress(int key, int scancode, int mod) {
        keys[key] = true;
        mods[mod] = true;
    }

    private void onKeyRelease(int key, int scancode, int mod) {
        if (key == -1) return;
        keys[key] = false;
        mods[mod] = false;
    }

    private void onKeyRepeat(int key, int scancode, int mod) {

    }

    private void onMouseButtonPress(int button, int mod) {

    }

    private void onMouseButtonRelease(int button, int mod) {

    }

    private void onMouseButtonRepeat(int button, int mod) {

    }

    private void onMouseMove(double xpos, double ypos) {
        Vector2i size = window.getSize();
        x = (double) size.x / 2.0;
        y = (double) size.y / 2.0;

        if (firstMouse) {
            glfwSetCursorPos(window.getHandle(), (int) x, (int) y);
            firstMouse = false;
            return;
        }

        glfwSetCursorPos(window.getHandle(), (int) x, (int) y);

        dx = (float) (xpos - x);
        dy = (float) (ypos - y);
    }

    private void onMouseScroll(double xoffset, double yoffset) {

    }

    private void init() {
        glfwSetWindowSizeCallback(window.getHandle(),
                windowSizeCallback = new GLFWWindowSizeCallback() {

                    @Override
                    public void invoke(long window, int width, int height) {
                        /*
                         * window - the window that received the event
                         * width - the new width
                         * height - the new height
                         */
                        onWindowSizeChanged(width, height);
                    }
                });

        glfwSetCursorPosCallback(window.getHandle(),
                cursorPosCallback = new GLFWCursorPosCallback() {

                    @Override
                    public void invoke(long window, double xpos, double ypos) {
                        /*
                         * window - the window that received the event
                         * xpos - the new absolute x-value of the cursor
                         * ypos - the new absolute y-value of the cursor
                         */
                        onMouseMove(xpos, ypos);
                    }
                });

        glfwSetKeyCallback(window.getHandle(),
                keyCallback = new GLFWKeyCallback() {

                    @Override
                    public void invoke(long window, int key, int scancode, int action, int mods) {
                        /*
                         * window - the window that received the event
                         * key - the keyboard key that was pressed or released
                         * scancode - the system-specific scancode of the key
                         * action - the key action [GLFW.GLFW_PRESS; GLFW.GLFW_RELEASE; GLFW.GLFW_REPEAT]
                         * mods - bitfield describing which modifier keys were held down
                         */
                        switch (action) {
                            case GLFW_PRESS -> onKeyPress(key, scancode, mods);
                            case GLFW_RELEASE -> onKeyRelease(key, scancode, mods);
                            case GLFW_REPEAT -> onKeyRepeat(key, scancode, mods);
                        }
                    }
                });

        glfwSetMouseButtonCallback(window.getHandle(),
                mouseButtonCallback = new GLFWMouseButtonCallback() {

                    @Override
                    public void invoke(long window, int button, int action, int mods) {
                        /*
                         * window - the window that received the event
                         * button - the mouse button that was pressed or released
                         * action - the key action [GLFW.GLFW_PRESS; GLFW.GLFW_RELEASE; GLFW.GLFW_REPEAT]
                         * mods - bitfield describing which modifier keys were held down
                         */
                        switch (action) {
                            case GLFW_PRESS -> onMouseButtonPress(button, mods);
                            case GLFW_RELEASE -> onMouseButtonRelease(button, mods);
                            case GLFW_REPEAT -> onMouseButtonRepeat(button, mods);
                        }
                    }
                });

        glfwSetScrollCallback(window.getHandle(),
                scrollCallback = new GLFWScrollCallback() {

                    @Override
                    public void invoke(long window, double xoffset, double yoffset) {
                        /*
                         * window - the window that received the event
                         * xoffset - the scroll offset along the x-axis
                         * yoffset - the scroll offset along the y-axis
                         */
                        onMouseScroll(xoffset, yoffset);
                    }
                });

        glfwSetWindowFocusCallback(window.getHandle(),
                windowFocusCallback = new GLFWWindowFocusCallback() {

                    @Override
                    public void invoke(long window, boolean focused) {
                        /*
                         * window - the window that received the event
                         * focused - [GL11.GL_TRUE; GL11.GL_FALSE]
                         */
                        onFocusChanged(focused);
                    }
                });
    }

    public void dispose() {
        windowSizeCallback.free();
        cursorPosCallback.free();
        keyCallback.free();
        mouseButtonCallback.free();
        scrollCallback.free();
        windowFocusCallback.free();
    }
}
