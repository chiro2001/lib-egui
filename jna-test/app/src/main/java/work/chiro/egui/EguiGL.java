package work.chiro.egui;

import static org.lwjgl.opengl.GL11C.GL_RENDERER;
import static org.lwjgl.opengl.GL11C.GL_VENDOR;
import static org.lwjgl.opengl.GL11C.GL_VERSION;
import static org.lwjgl.opengl.GL11C.glGetString;
import static org.lwjgl.opengl.GL45.GL_BLEND;
import static org.lwjgl.opengl.GL45.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL45.GL_LINEAR;
import static org.lwjgl.opengl.GL45.GL_ONE;
import static org.lwjgl.opengl.GL45.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL45.GL_QUADS;
import static org.lwjgl.opengl.GL45.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL45.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL45.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL45.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL45.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL45.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL45.GL_TRIANGLES;
import static org.lwjgl.opengl.GL45.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL45.GL_UNPACK_ROW_LENGTH;
import static org.lwjgl.opengl.GL45.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL45.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL45.glBindTexture;
import static org.lwjgl.opengl.GL45.glBlendFunc;
import static org.lwjgl.opengl.GL45.glClear;
import static org.lwjgl.opengl.GL45.glClearColor;
import static org.lwjgl.opengl.GL45.glDisable;
import static org.lwjgl.opengl.GL45.glDrawElements;
import static org.lwjgl.opengl.GL45.glEnable;
import static org.lwjgl.opengl.GL45.glGenTextures;
import static org.lwjgl.opengl.GL45.glPixelStorei;
import static org.lwjgl.opengl.GL45.glTexParameteri;
import static org.lwjgl.opengl.GL45.glViewport;
import static org.lwjgl.opengl.GL45.*;

public class EguiGL {
    public int eguiTexture;
    public int indexBuffer;
    public int posBuffer;
    public int tcBuffer;
    public int colorBuffer;
    public int vertexArray;
    public int program;

    public EguiGL() {
    }

    LibEgui.PainterBeforeHandler beforeHandler = () -> {
        try {
            // System.out.println("before");
            glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 4);

            // glClearColor(0x10, 0x10, 0x10, 0x10);
            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT);

            // int w = getWidth();
            // int h = getHeight();
            int w = 600;
            int h = 600;

            glEnable(GL_FRAMEBUFFER_SRGB);
            glEnable(GL_SCISSOR_TEST);
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            // glUseProgram(program);
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
    };
    LibEgui.PainterMeshHandler meshHandler = (minX, minY, maxX, maxY, indices, indicesLen, vertices, verticesLen, textureManaged, textureId) -> {
        // System.out.println("mesh");
        // Mesh mesh = new Mesh(indices, indicesLen, vertices, verticesLen, textureManaged, textureId);
        // paintMesh(mesh);
        int w = 600;
        int h = 600;
        float aspect = (float) w / h;
        double now = System.currentTimeMillis() * 0.001;
        float width = (float) Math.abs(Math.sin(now * 0.3));
        glClear(GL_COLOR_BUFFER_BIT);
        glViewport(0, 0, w, h);
        glBegin(GL_QUADS);
        glColor3f(0.4f, 0.6f, 0.8f);
        glVertex2f(-0.75f * width / aspect, 0.0f);
        glVertex2f(0, -0.75f);
        glVertex2f(+0.75f * width / aspect, 0);
        glVertex2f(0, +0.75f);
        glEnd();
    };

    LibEgui.VoidHandler afterHandler = () -> {
        // System.out.println("after");
        glDisable(GL_SCISSOR_TEST);
        glDisable(GL_FRAMEBUFFER_SRGB);
        glDisable(GL_BLEND);
    };

    public void init() {
        System.err.println("GL_VENDOR: " + glGetString(GL_VENDOR));
        System.err.println("GL_RENDERER: " + glGetString(GL_RENDERER));
        System.err.println("GL_VERSION: " + glGetString(GL_VERSION));
        glClearColor(0.3f, 0.4f, 0.5f, 1);
        eguiTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, eguiTexture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        int vertShader = Shader.compile(Shader.VS_SRC_150, GL_VERTEX_SHADER);
        int fragShader = Shader.compile(Shader.FS_SRC_150, GL_FRAGMENT_SHADER);
        // vertShader = Shader.compile(Shader.VS_SRC, GL_VERTEX_SHADER);
        // fragShader = Shader.compile(Shader.FS_SRC, GL_FRAGMENT_SHADER);
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

    public void paintMesh(Mesh mesh) {
        // glBindTexture(GL_TEXTURE_2D, eguiTexture);

        glBindVertexArray(vertexArray);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, mesh.indices, GL_STREAM_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, posBuffer);
        glBufferData(GL_ARRAY_BUFFER, mesh.positions, GL_STREAM_DRAW);

        int posLoc = glGetAttribLocation(program, "a_pos");
        assert posLoc > 0;
        int stride = 0;
        glVertexAttribPointer(posLoc, 2, GL_FLAT, false, stride, 0);
        glEnableVertexAttribArray(posLoc);

        glBindBuffer(GL_ARRAY_BUFFER, tcBuffer);
        glBufferData(GL_ARRAY_BUFFER, mesh.texCoords, GL_STREAM_DRAW);

        int tcLoc = glGetAttribLocation(program, "a_tc");
        assert tcLoc > 0;
        glVertexAttribPointer(tcLoc, 2, GL_FLAT, false, stride, 0);
        glEnableVertexAttribArray(tcLoc);

        glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
        glBufferData(GL_ARRAY_BUFFER, mesh.colors, GL_STREAM_DRAW);

        int srgbaLoc = glGetAttribLocation(program, "a_srgba");
        assert srgbaLoc > 0;
        glVertexAttribPointer(srgbaLoc, 4, GL_UNSIGNED_BYTE, false, stride, 0);
        glEnableVertexAttribArray(srgbaLoc);

        // glDrawElements(GL_TRIANGLES, mesh.indices);
        glDrawElements(GL_TRIANGLES, mesh.indicesLen >> 1, GL_UNSIGNED_SHORT, 0);
        glDisableVertexAttribArray(posLoc);
        glDisableVertexAttribArray(tcLoc);
        glDisableVertexAttribArray(srgbaLoc);
    }
}
