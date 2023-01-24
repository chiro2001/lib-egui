package work.chiro.egui;

import com.sun.jna.Pointer;

import java.nio.ByteBuffer;

public class Mesh {
    ByteBuffer indices;
    int indicesLen;
    float[] positions;
    float[] texCoords;
    ByteBuffer colors;
    Boolean textureManaged;
    Long textureId;

    public Mesh(Pointer indices, int indicesLen, Pointer vertices, int verticesLen, Boolean textureManaged, Long textureId) {
        this.indices = indices.getByteBuffer(0, (long) indicesLen << 2);
        this.indicesLen = indicesLen;
        positions = new float[verticesLen << 1];
        texCoords = new float[verticesLen << 1];
        colors = ByteBuffer.allocate(verticesLen << 2);
        for (int i = 0; i < verticesLen; i++) {
            int i2 = i << 1;
            int i4 = i << 2;
            Vertex v = Vertex.fromPointer(new Pointer(Pointer.nativeValue(vertices) + (long) i * Vertex.bytesLength()));
            positions[i2] = v.pos.x;
            positions[i2 + 1] = v.pos.y;
            texCoords[i2] = v.uv.x;
            texCoords[i2 + 1] = v.uv.y;
            colors.putInt((int) (v.color & 0xffffffffL));
        }
        this.textureManaged = textureManaged;
        this.textureId = textureId;
    }
}
