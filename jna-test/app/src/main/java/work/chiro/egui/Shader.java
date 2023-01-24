package work.chiro.egui;

import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.Charset;

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
}
