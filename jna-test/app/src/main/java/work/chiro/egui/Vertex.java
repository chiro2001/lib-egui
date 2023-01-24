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
        return new Vertex(new Pos2(0, 0), new Pos2(0, 0), 0L);
    }
}
