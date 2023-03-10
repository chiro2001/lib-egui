package work.chiro.egui;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface LibEgui extends Library {
    int test(int left, int right);

    interface PainterMeshHandler extends Callback {
        void callback(float minX, float minY, float maxX, float maxY,
                      Pointer indices, int indicesLen,
                      Pointer vertices, int verticesLen,
                      Boolean textureManaged, Long textureId);
    }

    interface PainterBeforeHandler extends Callback {
        boolean callback();
    }

    Pointer egui_create(PainterBeforeHandler before, PainterMeshHandler handler, VoidHandler after);

    void egui_run(Pointer egui);

    void egui_run_block(Pointer egui);

    interface VoidHandler extends Callback {
        void callback();
    }

    void call_void(VoidHandler handler);

    interface IntHandler extends Callback {
        void callback(int i);
    }

    void call_u32(IntHandler handler);

    interface VecHandler extends Callback {
        void callback(Pointer data, int len);
    }

    void call_vec(VecHandler handler);

    void egui_quit(Pointer egui);
}
