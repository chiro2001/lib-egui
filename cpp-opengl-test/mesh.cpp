//
// Created by chiro on 23-1-29.
//

#include <cstdio>
#include "gls.h"
#include "Shader.h"

GLuint program;

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
  glutCreateWindow("OpenGL Mesh Test"); // Create a window with the given title
  glutInitWindowSize(320, 320);   // Set the window's initial width & height
  glutInitWindowPosition(50, 50); // Position the window's initial top-left corner

  // disable stdio buffer
  setvbuf(stdout, nullptr, _IONBF, 0);
  printf("opengl version: %s", glGetString(GL_VERSION));

  auto shader = Shader("mesh_test");
  program = shader.program;

  glutDisplayFunc(display); // Register display callback handler for window re-paint
  glutMainLoop();           // Enter the infinitely event-processing loop
  return 0;
}