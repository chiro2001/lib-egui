package work.chiro.egui;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import java.util.concurrent.TimeUnit;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL45.*;
import static work.chiro.egui.App.signalTerminate;
import static work.chiro.egui.App.signalTerminated;

public class EguiGLCanvas extends AWTGLCanvas {
    private boolean enabled = false;
    public int eguiTexture;
    public int indexBuffer;
    public int posBuffer;
    public int tcBuffer;
    public int colorBuffer;
    public int vertexArray;
    public int vertShader;
    public int fragShader;
    public int program;
    private final LibEgui lib;
    private Pointer ui;

    protected EguiGLCanvas(GLData data) {
        super(data);
        String pwd = System.getProperty("user.dir");
        lib = Native.load(String.format("%s/../target/debug/libegui.so", pwd), LibEgui.class);
    }

    public void start() {
        ui = lib.egui_create(() -> {
            if (!enabled && initCalled) {
                System.out.println("canvas disabled!");
                try {
                    Thread.sleep(1000);
                } catch (Throwable e) {
                    System.out.printf("when canvas disabled: %s\n", e.toString());
                }
                // System.exit(0);
                return false;
            }
            if (!isValid()) {
                GL.setCapabilities(null);
                return false;
            }
            beforeRender();
            try {
                if (!initCalled) {
                    initGL();
                    initCalled = true;
                }

                glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
                glPixelStorei(GL_UNPACK_ALIGNMENT, 4);

                glClearColor(0x3c, 0x3f, 0x3f, 0xff);
                glClear(GL_COLOR_BUFFER_BIT);

                int w = getWidth();
                int h = getHeight();

                glEnable(GL_FRAMEBUFFER_SRGB);
                glEnable(GL_SCISSOR_TEST);
                glEnable(GL_BLEND);
                glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
                glUseProgram(program);
                glActiveTexture(GL_TEXTURE0);
                int screenSizeLoc = glGetUniformLocation(program, "u_screen_size");
                glUniform2f(screenSizeLoc, w, h);
                int samplerLoc = glGetUniformLocation(program, "u_sampler");
                glUniform1i(samplerLoc, 0);
                glViewport(0, 0, w, h);
            } catch (Throwable e) {
                System.out.printf("paint error: %s\n", e);
                return false;
            }

            return true;
        }, (minX, minY, maxX, maxY, indices, indicesLen, vertices, verticesLen, textureManaged, textureId) -> {
            Mesh mesh = new Mesh(indices, indicesLen, vertices, verticesLen, textureManaged, textureId);
            paintMesh(mesh);
        }, () -> {
            glDisable(GL_SCISSOR_TEST);
            glDisable(GL_FRAMEBUFFER_SRGB);
            glDisable(GL_BLEND);
            afterRender();

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
        });
        Thread thread = new Thread(() -> lib.egui_run_block(ui));
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void initGL() {
        System.out.println("OpenGL version: " + effective.majorVersion + "." + effective.minorVersion + " (Profile: " + effective.profile + ")");
        createCapabilities();
        glClearColor(0.3f, 0.4f, 0.5f, 1);
        enabled = true;
        eguiTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, eguiTexture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        vertShader = Shader.compile(Shader.VS_SRC, GL_VERTEX_SHADER);
        fragShader = Shader.compile(Shader.FS_SRC, GL_FRAGMENT_SHADER);
        program = Shader.linkProgram(vertShader, fragShader);

        vertexArray = glGenVertexArrays();
        glBindVertexArray(vertexArray);

        indexBuffer = glGenBuffers();
        posBuffer = glGenBuffers();
        tcBuffer = glGenBuffers();
        colorBuffer = glGenBuffers();

        glDetachShader(program, vertShader);
        glDetachShader(program, fragShader);
        glDeleteShader(vertShader);
        glDeleteShader(fragShader);
    }

    @Override
    public void paintGL() {
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
        glBindTexture(GL_TEXTURE_2D, eguiTexture);

        glBindVertexArray(vertexArray);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, mesh.indices, GL_STREAM_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, posBuffer);
        glBufferData(GL_ARRAY_BUFFER, mesh.positions, GL_STREAM_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, tcBuffer);
        glBufferData(GL_ARRAY_BUFFER, mesh.texCoords, GL_STREAM_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
        glBufferData(GL_ARRAY_BUFFER, mesh.colors, GL_STREAM_DRAW);

        glDrawElements(GL_TRIANGLES, mesh.indices);

    }
}
