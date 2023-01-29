//
// Created by chiro on 23-1-29.
//

#include <GL/gl.h>
#include <GL/glut.h>

static const char *shader_vert =
    "//顶点着色器\n"
    "#version 300 es                            \n"
    "layout(location = 0) in vec4 a_position;   // 位置变量的属性位置值为 0 \n"
    "layout(location = 1) in vec3 a_color;      // 颜色变量的属性位置值为 1\n"
    "out vec3 v_color;                          // 向片段着色器输出一个颜色                          \n"
    "void main()                                \n"
    "{                                          \n"
    "    v_color = a_color;                     \n"
    "    gl_Position = a_position;              \n"
    "};";
static const char *shader_frag =
    "//片段着色器\n"
    "#version 300 es\n"
    "precision mediump float;\n"
    "in vec3 v_color;\n"
    "out vec4 o_fragColor;\n"
    "void main()\n"
    "{\n"
    "    o_fragColor = vec4(v_color, 1.0);\n"
    "}";

void display() {
  glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set background color to black and opaque
  glClear(GL_COLOR_BUFFER_BIT);         // Clear the color buffer

  // Draw a Red 1x1 Square centered at origin
  glBegin(GL_QUADS);              // Each set of 4 vertices form a quad
  glColor3f(1.0f, 0.0f, 0.0f); // Red
  glVertex2f(-0.5f, -0.5f);    // x, y
  glVertex2f(0.5f, -0.5f);
  glVertex2f(0.5f, 0.5f);
  glVertex2f(-0.5f, 0.5f);
  glEnd();

  glFlush();  // Render now
}

int main(int argc, char *argv[]) {
  glutInit(&argc, argv);                 // Initialize GLUT
  glutCreateWindow("OpenGL Setup Test"); // Create a window with the given title
  glutInitWindowSize(320, 320);   // Set the window's initial width & height
  glutInitWindowPosition(50, 50); // Position the window's initial top-left corner
  glutDisplayFunc(display); // Register display callback handler for window re-paint
  glutMainLoop();           // Enter the infinitely event-processing loop
  return 0;
}