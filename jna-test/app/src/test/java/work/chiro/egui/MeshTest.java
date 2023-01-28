package work.chiro.egui;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import javax.swing.*;
import java.awt.*;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL45.*;

public class MeshTest {
    // static short[] indices = {2, 0, 4, 4, 0, 6, 0, 6, 7, 7, 1, 0, 2, 0, 1, 1, 3, 2, 4, 2, 3, 3, 5, 4, 6, 4, 5, 5, 7, 6, 8, 9, 10, 10, 9, 11, 12, 13, 14, 14, 13, 15, 16, 17, 18, 18, 17, 19, 20, 21, 22, 22, 21, 23, 24, 25, 26, 26, 25, 27, 28, 29, 30, 30, 29, 31, 32, 33, 34, 34, 33, 35, 36, 37, 38, 38, 37, 39};
    // static float[] points = {0.5f, 0.5f, -0.5f, -0.5f, 9999.5f, 0.5f, 10000.5f, -0.5f, 9999.5f, 9999.5f, 10000.5f, 10000.5f, 0.5f, 9999.5f, -0.5f, 10000.5f, 4981.0f, 4994.0f, 4983.0f, 4994.0f, 4981.0f, 5005.0f, 4983.0f, 5005.0f, 4983.0f, 4994.0f, 4986.0f, 4994.0f, 4983.0f, 5004.0f, 4986.0f, 5004.0f, 4987.0f, 4994.0f, 4993.0f, 4994.0f, 4987.0f, 5005.0f, 4993.0f, 5005.0f, 4993.0f, 4999.0f, 4997.0f, 4999.0f, 4993.0f, 5001.0f, 4997.0f, 5001.0f, 4997.0f, 4997.0f, 5004.0f, 4997.0f, 4997.0f, 5005.0f, 5004.0f, 5005.0f, 5004.0f, 4997.0f, 5011.0f, 4997.0f, 5004.0f, 5007.0f, 5011.0f, 5007.0f, 5011.0f, 4997.0f, 5018.0f, 4997.0f, 5011.0f, 5005.0f, 5018.0f, 5005.0f, 5018.0f, 4994.0f, 5021.0f, 4994.0f, 5018.0f, 5004.0f, 5021.0f, 5004.0f};
    // static float[] uvs = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.7792969f, 0.0f, 0.78027344f, 0.0f, 0.7792969f, 0.04296875f, 0.78027344f, 0.04296875f, 0.77197266f, 0.0f, 0.7734375f, 0.0f, 0.77197266f, 0.0390625f, 0.7734375f, 0.0390625f, 0.7475586f, 0.0f, 0.7504883f, 0.0f, 0.7475586f, 0.04296875f, 0.7504883f, 0.04296875f, 0.5527344f, 0.0f, 0.5546875f, 0.0f, 0.5527344f, 0.0078125f, 0.5546875f, 0.0078125f, 0.7583008f, 0.0f, 0.76171875f, 0.0f, 0.7583008f, 0.03125f, 0.76171875f, 0.03125f, 0.76464844f, 0.0f, 0.7680664f, 0.0f, 0.76464844f, 0.0390625f, 0.7680664f, 0.0390625f, 0.80810547f, 0.0f, 0.81152344f, 0.0f, 0.80810547f, 0.03125f, 0.81152344f, 0.03125f, 0.77197266f, 0.0f, 0.7734375f, 0.0f, 0.77197266f, 0.0390625f, 0.7734375f, 0.0390625f};
    // static long[] colors = {454761471L, 0L, 454761471L, 0L, 454761471L, 0L, 454761471L, 0L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L, 2358021375L};

    static short[] indices = {0, 1, 2, 2, 1, 3};
    static float[] points = {0.0f, 0.0f, 200.0f, 0.0f, 100.0f, 200.0f, 300.0f, 200.0f};
    static float[] uvs = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    static long[] colors = {454761471L, 0L, 454761471L, 0L, 454761471L};

    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("AWT test");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setPreferredSize(new Dimension(600, 600));
        GLData data = new GLData();
        AWTGLCanvas canvas;
        frame.add(canvas = new AWTGLCanvas(data) {
            private int colorBuffer;
            private int tcBuffer;
            private int posBuffer;
            private int indexBuffer;
            private int vertexArray;
            private int program;
            private int eguiTexture;
            private static final long serialVersionUID = 1L;

            @Override
            public void initGL() {
                System.out.println("OpenGL version: " + effective.majorVersion + "." + effective.minorVersion + " (Profile: " + effective.profile + ")");
                createCapabilities();
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

            @Override
            public void paintGL() {
                glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
                glPixelStorei(GL_UNPACK_ALIGNMENT, 4);

                int screenWidth = 600;
                int screenHeight = 600;

                // glEnable(GL_FRAMEBUFFER_SRGB);
                // glEnable(GL_SCISSOR_TEST);
                // glEnable(GL_BLEND);
                // glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
                // glUseProgram(program);
                glActiveTexture(GL_TEXTURE0);
                // int screenSizeLoc = glGetUniformLocation(program, "u_screen_size");
                // glUniform2f(screenSizeLoc, screenWidth, screenHeight);
                // int samplerLoc = glGetUniformLocation(program, "u_sampler");
                // glUniform1i(samplerLoc, 0);
                // glViewport(0, 0, screenWidth, screenHeight);

                glColor3b((byte) 0xff, (byte) 0xff, (byte) 0xff);

                glBindVertexArray(vertexArray);
                glEnableClientState(GL_VERTEX_ARRAY);
                // glVertexPointer(2, GL_FLOAT, 0, 0);
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STREAM_DRAW);

                glBindBuffer(GL_ARRAY_BUFFER, posBuffer);
                glBufferData(GL_ARRAY_BUFFER, points, GL_STREAM_DRAW);

                // int posLoc = glGetAttribLocation(program, "a_pos");
                // assert posLoc >= 0;
                // int stride = 0;
                // glVertexAttribPointer(posLoc, 2, GL_FLOAT, false, stride, 0);
                // glEnableVertexAttribArray(posLoc);
                //
                // glBindBuffer(GL_ARRAY_BUFFER, tcBuffer);
                // glBufferData(GL_ARRAY_BUFFER, uvs, GL_STREAM_DRAW);
                //
                // int tcLoc = glGetAttribLocation(program, "a_tc");
                // assert tcLoc >= 0;
                // glVertexAttribPointer(tcLoc, 2, GL_FLOAT, false, stride, 0);
                // glEnableVertexAttribArray(tcLoc);
                //
                // glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
                // glBufferData(GL_ARRAY_BUFFER, colors, GL_STREAM_DRAW);
                //
                // int srgbaLoc = glGetAttribLocation(program, "a_srgba");
                // assert srgbaLoc > 0;
                // glVertexAttribPointer(srgbaLoc, 4, GL_UNSIGNED_BYTE, false, stride, 0);
                // glEnableVertexAttribArray(srgbaLoc);
                //
                // // glDrawElements(GL_TRIANGLES, ShortBuffer.wrap(indices));
                glDrawElements(GL_TRIANGLES, indices.length >> 1, GL_UNSIGNED_SHORT, 0);
                // glDisableVertexAttribArray(posLoc);
                // glDisableVertexAttribArray(tcLoc);
                // glDisableVertexAttribArray(srgbaLoc);
                //
                // glDisable(GL_SCISSOR_TEST);
                // glDisable(GL_FRAMEBUFFER_SRGB);
                // glDisable(GL_BLEND);

                swapBuffers();
            }
        }, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.transferFocus();
        Runnable renderLoop = new Runnable() {
            @Override
            public void run() {
                if (!canvas.isValid()) {
                    GL.setCapabilities(null);
                    return;
                }
                canvas.render();
                SwingUtilities.invokeLater(this);
            }
        };
        SwingUtilities.invokeLater(renderLoop);
    }
}
