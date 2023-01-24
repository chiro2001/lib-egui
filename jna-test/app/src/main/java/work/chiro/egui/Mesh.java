package work.chiro.egui;

import com.sun.jna.Pointer;

public class Mesh {
    Pointer indices;
    int indicesLen;
    Pointer vertices;
    int verticesLen;
    Boolean textureManaged;
    Long textureId;

    public Mesh(Pointer indices, int indicesLen, Pointer vertices, int verticesLen, Boolean textureManaged, Long textureId) {
        this.indices = indices;
        this.indicesLen = indicesLen;
        this.vertices = vertices;
        this.verticesLen = verticesLen;
        this.textureManaged = textureManaged;
        this.textureId = textureId;
    }
}
