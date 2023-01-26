/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package work.chiro.egui;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.awt.GLData;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class App {
    private final JFrame frame;
    private LibEgui lib;
    private Pointer ui;
    EguiGL egui;
    MyGLCanvas canvas;
    static Thread thread = null;

    boolean terminated = false;

    public void doTerminate() throws InterruptedException {
        if (terminated) return;
        terminated = true;
        System.out.println("doTerminate done");
        System.out.println("main acquiring terminated");
        lib.egui_quit(ui);
        thread.join();
        System.out.println("main: dispose canvas");
        GL.setCapabilities(null);
        System.out.println("main: dispose canvas done");
        System.out.println("all done");
        System.out.println("main: disposing frame");
        frame.dispose();
        System.out.println("main: disposed frame");
        System.out.println("run done");
        Thread.currentThread().interrupt();
    }

    public App() {
        frame = new JFrame("egui OpenGL Test") {
            @Override
            public void dispose() {
                try {
                    doTerminate();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                super.dispose();
            }
        };
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setPreferredSize(new Dimension(600, 600));
        GLData data = new GLData();
        data.samples = 4;
        data.swapInterval = 0;
        String pwd = System.getProperty("user.dir");
        lib = Native.load(String.format("%s/../target/debug/libegui.so", pwd), LibEgui.class);
        egui = new EguiGL();
        canvas = new MyGLCanvas(data, egui::init);
        AtomicInteger count = new AtomicInteger();
        JLabel label = new JLabel("counting");
        ui = lib.egui_create(() -> {
            count.getAndIncrement();
            String text = String.format("frame: %d", count.get());
            label.setText(text);

            // if (canvas == null) return false;
            if (!canvas.isValid()) {
                GL.setCapabilities(null);
                return false;
            }
            canvas.beforeRender();
            if (!canvas.getInitCalled()) {
                canvas.initGL();
                canvas.setInitCalled();
            }
            boolean r = egui.beforeHandler.callback();
            if (!r) {
                canvas.afterRender();
            }
            return r;
        }, egui.meshHandler, () -> {
            egui.afterHandler.callback();
            canvas.swapBuffers();
            canvas.afterRender();
        });
        frame.add(canvas, BorderLayout.CENTER);
        frame.add(label, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
        frame.transferFocus();
    }

    public void run() {
        lib.egui_run_block(ui);
    }

    public static void main(String[] args) {
        App app = new App();
        app.run();
        System.out.println("exit");
    }
}
