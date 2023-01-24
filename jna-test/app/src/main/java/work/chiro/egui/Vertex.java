package work.chiro.egui;

import com.sun.jna.Pointer;

public class Vertex {
    public Pos2 pos, uv;
    public Long color;

    public Vertex(Pos2 pos, Pos2 uv, Long color) {
        this.pos = pos;
        this.uv = uv;
        this.color = color;
    }

    public static Vertex fromPointer(Pointer buffer) {
        // System.out.printf("loading from addr: %x\n", Pointer.nativeValue(buffer));
        return new Vertex(
                new Pos2(
                        buffer.getFloat(0),
                        buffer.getFloat(4)
                ),
                new Pos2(
                        buffer.getFloat(8),
                        buffer.getFloat(12)
                ), buffer.getInt(16) & 0xffffffffL);
    }

    public static int bytesLength() {
        return 20;
    }
}
