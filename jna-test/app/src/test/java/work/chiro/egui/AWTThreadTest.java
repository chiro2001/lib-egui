package work.chiro.egui;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;

public class AWTThreadTest {
    abstract static class AWTGLCanvasExplicitDispose extends AWTGLCanvas {
        protected AWTGLCanvasExplicitDispose(GLData data) {
            super(data);
        }

        @Override
        public void disposeCanvas() {
        }

        public void doDisposeCanvas() {
            super.disposeCanvas();
        }
    }

    static Semaphore signalTerminate = new Semaphore(0);
    static Semaphore signalTerminated = new Semaphore(0);

    public static void doTerminate() {
        // request the cleanup
        signalTerminate.release();
        try {
            // wait until the thread is done with the cleanup
            signalTerminated.acquire();
        } catch (InterruptedException ignored) {
        }
    }

    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("AWT test") {
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
        AWTGLCanvasExplicitDispose canvas;
        frame.add(canvas = new AWTGLCanvasExplicitDispose(data) {
            private static final long serialVersionUID = 1L;

            @Override
            public void initGL() {
                System.out.println("OpenGL version: " + effective.majorVersion + "." + effective.minorVersion + " (Profile: " + effective.profile + ")");
                createCapabilities();
                glClearColor(0.3f, 0.4f, 0.5f, 1);
            }

            @Override
            public void paintGL() {
                int w = getWidth();
                int h = getHeight();
                float aspect = (float) w / h;
                double now = System.currentTimeMillis() * 0.001;
                float width = (float) Math.abs(Math.sin(now * 0.3));
                glClear(GL_COLOR_BUFFER_BIT);
                glViewport(0, 0, w, h);
                glBegin(GL_QUADS);
                glColor3f(0.4f, 0.6f, 0.8f);
                glVertex2f(-0.75f * width / aspect, 0.0f);
                glVertex2f(0, -0.75f);
                glVertex2f(+0.75f * width / aspect, 0);
                glVertex2f(0, +0.75f);
                glEnd();
                swapBuffers();
            }

        }, BorderLayout.CENTER);
        AtomicInteger count = new AtomicInteger();
        JLabel label = new JLabel("counting");
        frame.add(label, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
        frame.transferFocus();

        Runnable renderLoop = () -> {
            while (true) {
                canvas.render();
                label.setText(String.format("frame: %d", count.incrementAndGet()));
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    canvas.doDisposeCanvas();
                    break;
                }
                // try {
                //     if (signalTerminate.tryAcquire(10, TimeUnit.MILLISECONDS)) {
                //         GL.setCapabilities(null);
                //         canvas.doDisposeCanvas();
                //         signalTerminated.release();
                //         return;
                //     }
                // } catch (InterruptedException ignored) {
                // }
            }
            System.out.println("renderLoop exit");
        };
        Thread renderThread = new Thread(renderLoop);
        renderThread.start();
        // Thread.sleep(3000);
        // canvas.disposeCanvas();
        // canvas.doDisposeCanvas();
        // Thread.sleep(3000);
        // renderThread.interrupt();
        renderThread.join();
        // canvas.doDisposeCanvas();
        // renderThread.interrupt();
        // doTerminate();
        // canvas.disposeCanvas();
        frame.dispose();
    }
}