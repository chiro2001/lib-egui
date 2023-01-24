package work.chiro.egui;

import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.lwjgl.opengl.GL45.*;

public class Shader {
    public static final String VS_SRC_150;
    public static final String FS_SRC_150;
    public static final String VS_SRC;
    public static final String FS_SRC;

    static {
        try {
            VS_SRC_150 = Resources.toString(ClassLoader.getSystemResource("shaders/vs_150.shader"), Charset.defaultCharset());
            FS_SRC_150 = Resources.toString(ClassLoader.getSystemResource("shaders/fs_150.shader"), Charset.defaultCharset());
            VS_SRC = Resources.toString(ClassLoader.getSystemResource("shaders/vs.shader"), Charset.defaultCharset());
            FS_SRC = Resources.toString(ClassLoader.getSystemResource("shaders/fs.shader"), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int compile(String src, int typ) {
        int shader = glCreateShader(typ);
        glShaderSource(shader, src);
        glCompileShader(shader);
        int[] status = new int[]{GL_FALSE};
        glGetShaderiv(shader, GL_COMPILE_STATUS, status);
        if (status[0] != GL_TRUE) {
            String log = glGetShaderInfoLog(shader);
            throw new RuntimeException(String.format("cannot compile shader: %s", log));
        }
        return shader;
    }

    public static int linkProgram(int vs, int fs) {
        int program = glCreateProgram();
        glAttachShader(program, vs);
        glAttachShader(program, fs);
        glLinkProgram(program);
        int[] status = new int[]{GL_FALSE};
        glGetProgramiv(program, GL_LINK_STATUS, status);
        if (status[0] != GL_TRUE) {
            String log = glGetProgramInfoLog(program);
            throw new RuntimeException(String.format("cannot link program: %s", log));
        }
        return program;
    }
}
