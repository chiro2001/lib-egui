package work.chiro.egui;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface LibEGui extends Library {
    int test(int left, int right);

    interface MeshPainterHandler extends Callback {
        void callback(Pointer indices, int indicesLen, Pointer vertices, int verticesLen, Boolean textureManaged, Long textureId);
    }

    Pointer egui_create(MeshPainterHandler handler);

    void egui_run(Pointer egui);
}
