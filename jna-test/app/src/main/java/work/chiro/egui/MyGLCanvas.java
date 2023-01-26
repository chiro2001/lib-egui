package work.chiro.egui;

import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import static org.lwjgl.opengl.GL.createCapabilities;

public class MyGLCanvas extends AWTGLCanvas {
    Runnable inits;

    public MyGLCanvas(GLData data, Runnable inits) {
        super(data);
        this.inits = inits;
    }

    @Override
    public void initGL() {
        System.out.println("OpenGL version: " + effective.majorVersion + "." + effective.minorVersion + " (Profile: " + effective.profile + ")");
        createCapabilities();
        inits.run();
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

    public boolean getInitCalled() {
        return initCalled;
    }

    public void setInitCalled() {
        initCalled = true;
    }

    @Override
    public void disposeCanvas() {
        // super.disposeCanvas();
    }

    public void doDisposeCanvas() {
        super.disposeCanvas();
    }
}
