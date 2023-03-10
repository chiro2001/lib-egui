/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package work.chiro.egui.gears;

import org.lwjgl.glfw.GLFWAllocator;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static work.chiro.egui.gears.GLFWUtil.glfwInvoke;

/**
 * The Gears demo implemented using GLFW.
 */
public class Gears {

    private GLFWAllocator allocator;
    private Callback debugProc;

    private long window;

    private int xpos;
    private int ypos;
    private int width;
    private int height;

    private GLXGears gears;

    public static void main(String[] args) {
        new Gears().run();
    }

    private void run() {
        try {
            init();

            loop();
        } finally {
            try {
                destroy();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void framebufferSizeChanged(long window, int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }

        gears.setSize(width, height);
    }

    private void init() {
        allocator = GLFWAllocator.calloc()
                .allocate((size, user) -> nmemAllocChecked(size))
                .reallocate((block, size, user) -> nmemReallocChecked(block, size))
                .deallocate((block, user) -> nmemFree(block));

        glfwInitAllocator(allocator);

        GLFWErrorCallback.createPrint().set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize glfw");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        if (Platform.get() == Platform.MACOSX) {
            glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);
        }

        int WIDTH = 300;
        int HEIGHT = 300;

        long window = glfwCreateWindow(WIDTH, HEIGHT, "GLFW Gears Demo", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

		/*
        // This code did the equivalent of glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE) before GLFW 3.3
		if ( Platform.get() == Platform.MACOSX ) {
			long cocoaWindow = glfwGetCocoaWindow(window);

			long objc_msgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");
			long contentView = invokePPP(cocoaWindow, sel_getUid("contentView"), objc_msgSend);

			invokePPV(contentView, sel_getUid("setWantsBestResolutionOpenGLSurface:"), false, objc_msgSend);

			boolean bool = invokePPZ(contentView, sel_getUid("wantsBestResolutionOpenGLSurface"), objc_msgSend);
			System.out.println("wantsBestResolutionOpenGLSurface = " + bool);
		}
		*/

        glfwSetWindowSizeLimits(window, WIDTH, HEIGHT, GLFW_DONT_CARE, GLFW_DONT_CARE);
        //glfwSetWindowAspectRatio(window, 1, 1);

        long monitor = glfwGetPrimaryMonitor();

        GLFWVidMode vidmode = glfwGetVideoMode(monitor);
        // Center window
        assert vidmode != null;
        glfwSetWindowPos(
                window,
                (vidmode.width() - WIDTH) / 2,
                (vidmode.height() - HEIGHT) / 2
        );

        glfwSetKeyCallback(window, (windowHnd, key, scancode, action, mods) -> {
            if (action != GLFW_RELEASE) {
                return;
            }

            switch (key) {
                case GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(windowHnd, true);
                case GLFW_KEY_A -> glfwRequestWindowAttention(windowHnd);
                case GLFW_KEY_F -> {
                    if (glfwGetWindowMonitor(windowHnd) == NULL) {
                        try (MemoryStack s = stackPush()) {
                            IntBuffer a = s.ints(0);
                            IntBuffer b = s.ints(0);

                            glfwGetWindowPos(windowHnd, a, b);
                            xpos = a.get(0);
                            ypos = b.get(0);

                            glfwGetWindowSize(windowHnd, a, b);
                            width = a.get(0);
                            height = b.get(0);
                        }
                        glfwSetWindowMonitor(windowHnd, monitor, 0, 0, vidmode.width(), vidmode.height(), vidmode.refreshRate());
                        glfwSwapInterval(1);
                    }
                }
                case GLFW_KEY_G ->
                        glfwSetInputMode(windowHnd, GLFW_CURSOR, glfwGetInputMode(windowHnd, GLFW_CURSOR) == GLFW_CURSOR_NORMAL
                                ? GLFW_CURSOR_DISABLED
                                : GLFW_CURSOR_NORMAL
                        );
                case GLFW_KEY_O -> glfwSetWindowOpacity(window, glfwGetWindowOpacity(window) == 1.0f ? 0.5f : 1.0f);
                case GLFW_KEY_R ->
                        glfwSetWindowAttrib(windowHnd, GLFW_RESIZABLE, 1 - glfwGetWindowAttrib(windowHnd, GLFW_RESIZABLE));
                case GLFW_KEY_U ->
                        glfwSetWindowAttrib(windowHnd, GLFW_DECORATED, 1 - glfwGetWindowAttrib(windowHnd, GLFW_DECORATED));
                case GLFW_KEY_W -> {
                    if (glfwGetWindowMonitor(windowHnd) != NULL) {
                        glfwSetWindowMonitor(windowHnd, NULL, xpos, ypos, width, height, 0);
                    }
                }
            }
        });

        glfwSetFramebufferSizeCallback(window, this::framebufferSizeChanged);

        glfwSetWindowRefreshCallback(window, windowHnd -> {
            gears.render();
            gears.animate();
            glfwSwapBuffers(windowHnd);
        });

        glfwMakeContextCurrent(window);
        // GL.createCapabilities(MemoryUtil::memCallocPointer);
        // GL.createCapabilities(n -> MemoryUtil.memCallocPointer(n));
        GL.createCapabilities();
        debugProc = GLUtil.setupDebugMessageCallback();

        gears = new GLXGears();

        glfwSwapInterval(1);
        glfwShowWindow(window);

        glfwInvoke(window, null, this::framebufferSizeChanged);

        this.window = window;
    }

    private void loop() {
        long lastUpdate = System.currentTimeMillis();

        int frames = 0;
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();

            gears.render();
            gears.animate();

            glfwSwapBuffers(window);

            frames++;

            long time = System.currentTimeMillis();

            int UPDATE_EVERY = 5; // seconds
            if (UPDATE_EVERY * 1000L <= time - lastUpdate) {
                lastUpdate = time;

                System.out.printf("%d frames in %d seconds = %.2f fps\n", frames, UPDATE_EVERY, (frames / (float) UPDATE_EVERY));
                frames = 0;
            }
        }
    }

    private void destroy() {
        memFree(GL.getCapabilities().getAddressBuffer());
        GL.setCapabilities(null);

        if (debugProc != null) {
            debugProc.free();
        }

        if (window != NULL) {
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
        }

        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();

        allocator.deallocate().free();
        allocator.reallocate().free();
        allocator.allocate().free();
        allocator.free();
    }

}