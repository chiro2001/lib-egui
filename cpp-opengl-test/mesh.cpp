//
// Created by chiro on 23-1-29.
//

#include <cstdio>
#include "gls.h"
#include "Shader.h"

GLuint program;
GLuint vbo[2];

const GLfloat vertices[] = {
    -0.5f, 0.5f, 0.0f,       // v0
    1.0f, 0.0f, 0.0f,        // c0
    -0.5f, -0.5f, 0.0f,      // v1
    0.0f, 1.0f, 0.0f,        // c1
    0.5f, -0.5f, 0.0f,       // v2
    0.0f, 0.0f, 1.0f,        // c2
    0.5f, 0.5f, 0.0f,        // v3
    0.5f, 1.0f, 1.0f,        // c3
};
const GLushort indices[] = {0, 1, 2, 0, 2, 3};

void display() {
  glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set background color to black and opaque
  glClear(GL_COLOR_BUFFER_BIT);         // Clear the color buffer

  // glEnableVertexAttribArray(0);
  // glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 6 * sizeof(GLfloat), vertices);
  // glEnableVertexAttribArray(1);
  // glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 6 * sizeof(GLfloat), vertices + 3);
  // glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, indices);

  // draw as vbo
  glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
  glEnableVertexAttribArray(0);
  glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 6 * sizeof(GLfloat), nullptr);
  glEnableVertexAttribArray(1);
  glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 6 * sizeof(GLfloat),
                        reinterpret_cast<const void *>(3 * sizeof(GLfloat)));
  glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[1]);
  glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, nullptr);

  glFlush();  // Render now
}

int main(int argc, char *argv[]) {
  glutInit(&argc, argv);                 // Initialize GLUT
  glutCreateWindow("OpenGL Mesh Test"); // Create a window with the given title
  glutInitWindowSize(600, 600);   // Set the window's initial width & height
  glutInitWindowPosition(50, 50); // Position the window's initial top-left corner

  // disable stdio buffer
  setvbuf(stdout, nullptr, _IONBF, 0);
  printf("opengl version: %s", glGetString(GL_VERSION));

  auto shader = Shader("mesh_test");
  program = shader.program;
  glUseProgram(program);

  // prepare vbo data
  glGenBuffers(2, vbo);
  glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
  glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
  glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[1]);
  glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);

  glutDisplayFunc(display); // Register display callback handler for window re-paint
  glutMainLoop();           // Enter the infinitely event-processing loop
  return 0;
}