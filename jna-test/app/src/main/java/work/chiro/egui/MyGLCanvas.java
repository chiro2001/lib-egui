package work.chiro.egui;

import com.sun.jna.Pointer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import java.util.concurrent.TimeUnit;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL45.*;
import static work.chiro.egui.App.signalTerminate;
import static work.chiro.egui.App.signalTerminated;

public class MyGLCanvas extends AWTGLCanvas implements LibEGui.PainterHandler {
    private boolean enabled = false;
    public int eguiTexture;
    public int indexBuffer;
    public int posBuffer;
    public int colorBuffer;
    public int vertexArray;

    protected MyGLCanvas(GLData data) {
        super(data);
    }

    @Override
    public void initGL() {
        System.out.println("OpenGL version: " + effective.majorVersion + "." + effective.minorVersion + " (Profile: " + effective.profile + ")");
        createCapabilities();
        glClearColor(0.3f, 0.4f, 0.5f, 1);
        enabled = true;
        eguiTexture = glGenTextures();
        indexBuffer = glGenBuffers();
        colorBuffer = glGenBuffers();
        vertexArray = glGenVertexArrays();
        glBindVertexArray(vertexArray);
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
        super.disposeCanvas();
        enabled = false;
        // glDeleteBuffers(indexBuffer);
        // glDeleteBuffers(posBuffer);
        // glDeleteBuffers(colorBuffer);
        // glDeleteTextures(eguiTexture);
        // glDeleteVertexArrays(vertexArray);
    }

    public void paintMesh(Mesh mesh) {
        int w = getWidth();
        int h = getHeight();

        glBindTexture(GL_TEXTURE_2D, eguiTexture);

        glClear(GL_COLOR_BUFFER_BIT);
        glViewport(0, 0, w, h);
        glBegin(GL_TRIANGLES);
        // glColor3f(0.4f, 0.6f, 0.8f);
        for (int i = 0; i < mesh.indicesLen; i++) {
            int index = mesh.indices.getInt((long) i << 2);
            Vertex v = Vertex.fromPointer(new Pointer(Pointer.nativeValue(mesh.vertices) + (long) index * Vertex.bytesLength()));
            glVertex2f(v.pos.x, v.pos.y);
            glColor4b((byte) ((v.color >> 24) & 0xff), (byte) ((v.color >> 16) & 0xff), (byte) ((v.color >> 8) & 0xff), (byte) (v.color & 0xff));
        }
        glEnd();
        swapBuffers();
    }

    @Override
    public void callback(float minX, float minY, float maxX, float maxY, Pointer indices, int indicesLen, Pointer vertices, int verticesLen, Boolean textureManaged, Long textureId) {
        if (!isEnabled() && getInitCalled()) {
            System.out.println("canvas disabled!");
            return;
        }
        if (!isValid()) {
            GL.setCapabilities(null);
            return;
        }
        Mesh mesh = new Mesh(indices, indicesLen, vertices, verticesLen, textureManaged, textureId);
        beforeRender();
        try {
            if (!getInitCalled()) {
                initGL();
                setInitCalled(true);
            }
            paintMesh(mesh);
        } catch (Throwable e) {
            System.out.printf("paint error: %s\n", e);
        } finally {
            afterRender();
        }

        try {
            if (signalTerminate.tryAcquire(10, TimeUnit.MILLISECONDS)) {
                System.out.println("interrupted");
                GL.setCapabilities(null);
                signalTerminated.release();
                disposeCanvas();
            }
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
            System.out.println("InterruptedException");
        }
    }
}
