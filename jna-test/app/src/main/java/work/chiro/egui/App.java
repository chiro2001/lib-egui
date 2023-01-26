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
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class App {
    static Semaphore signalTerminate = new Semaphore(0);
    static Semaphore signalTerminated = new Semaphore(0);
    private final JFrame frame;
    private LibEgui lib;
    private Pointer ui;
    EguiGL egui;
    static MyGLCanvas canvas = null;
    static Thread thread = null;

    public static void doTerminate() {
        System.out.println("request the cleanup");
        signalTerminate.release();
        try {
            System.out.println("wait until the thread is done with the cleanup");
            thread.interrupt();
            boolean _i = signalTerminated.tryAcquire(10000, TimeUnit.MILLISECONDS);
            // signalTerminated.acquire();
            if (canvas != null) {
                GL.setCapabilities(null);
                System.out.println("do terminate: dispose canvas");
                canvas.disposeCanvas();
                System.out.println("do terminate: dispose canvas done");
            }
        } catch (InterruptedException ignored) {
        }
        System.out.println("doTerminate done");
    }

    public App() {
        frame = new JFrame("egui OpenGL Test") {
            @Override
            public void dispose() {
                // doTerminate();
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
            System.out.println(text);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (canvas == null) return false;
            if (!canvas.isValid()) {
                GL.setCapabilities(null);
                return false;
            }
            canvas.beforeRender();
            if (!canvas.getInitCalled()) {
                canvas.initGL();
                canvas.setInitCalled();
                return false;
            }
            return egui.beforeHandler.callback();
            // return false;
        }, (minX, minY, maxX, maxY, indices, indicesLen, vertices, verticesLen, textureManaged, textureId) -> {
            egui.meshHandler.callback(minX, minY, maxX, maxY, indices, indicesLen, vertices, verticesLen, textureManaged, textureId);
            // canvas.swapBuffers();
        }, () -> {
            egui.afterHandler.callback();
            if (canvas != null) {
                canvas.swapBuffers();
                canvas.afterRender();
            }
        });
        // ui = lib.egui_create(() -> {
        //     count.getAndIncrement();
        //     String text = String.format("counting: %d", count.get());
        //     label.setText(text);
        //     System.out.println(text);
        //     try {
        //         Thread.sleep(100);
        //     } catch (InterruptedException e) {
        //         throw new RuntimeException(e);
        //     }
        //     return false;
        // }, (minX, minY, maxX, maxY, indices, indicesLen, vertices, verticesLen, textureManaged, textureId) -> {
        // }, () -> {
        // });
        frame.add(canvas, BorderLayout.CENTER);
        frame.add(label, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
        // frame.transferFocus();
    }

    public void run() throws InterruptedException {
        thread = new Thread(() -> {
            lib.egui_run_block(ui);
            System.out.println("egui_run_block done");
        });
        thread.setDaemon(true);
        thread.start();
        egui.setQuitListener(() -> {
            // System.out.println("before egui_quit");
            // lib.egui_quit(ui);
            // System.out.println("after egui_quit");
            // frame.setVisible(false);
            // System.out.println("after unset visible");
            // frame.dispose();
            // System.out.println("after dispose");
            System.out.println("quit listener: dispose canvas");
            // canvas.disposeCanvas();
            GL.setCapabilities(null);
            canvas.afterRender();
            canvas.doDisposeCanvas();
            canvas = null;
        });

        Thread.sleep(1000);
        signalTerminate.release();
        System.out.println("main acquiring terminated");
        // signalTerminated.acquire();
        lib.egui_quit(ui);
        // Thread.sleep(1000);
        thread.join();
        thread = null;
        ui = null;
        lib = null;
        egui = null;
        System.out.println("main: dispose canvas");
        GL.setCapabilities(null);
        // canvas.disposeCanvas();
        System.out.println("main: dispose canvas done");
        System.out.println("all done");
        // doTerminate();
        // thread.interrupt();
        // canvas.disposeCanvas();
        // System.out.println("remove canvas");
        // // canvas.doDisposeCanvas();
        // frame.remove(canvas);
        // canvas = null;
        // System.out.println("remove canvas done");
        System.out.println("main: disposing frame");
        frame.dispose();
        System.out.println("main: disposed frame");
        // lib.egui_quit(ui);
        // System.exit(0);
        System.out.println("run done");
    }

    public static void main(String[] args) throws InterruptedException {
        App app = new App();
        app.run();
    }
}
