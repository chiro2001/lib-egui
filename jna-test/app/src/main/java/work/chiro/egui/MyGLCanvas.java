package work.chiro.egui;

import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.glClearColor;

public class MyGLCanvas extends AWTGLCanvas {
    private boolean enabled = false;

    protected MyGLCanvas(GLData data) {
        super(data);
    }

    @Override
    public void initGL() {
        System.out.println("OpenGL version: " + effective.majorVersion + "." + effective.minorVersion + " (Profile: " + effective.profile + ")");
        createCapabilities();
        glClearColor(0.3f, 0.4f, 0.5f, 1);
        enabled = true;
    }

    @Override
    public void paintGL() {

    }

    @Override
    public void beforeRender() {
        super.beforeRender();
    }

    @Override
    public void afterRender() {
        super.afterRender();
    }

    boolean getInitCalled() {
        return initCalled;
    }

    void setInitCalled(boolean value) {
        initCalled = value;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void disposeCanvas() {
        enabled = false;
        super.disposeCanvas();
    }
}
