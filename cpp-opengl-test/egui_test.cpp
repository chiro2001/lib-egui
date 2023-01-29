//
// Created by chiro on 23-1-29.
//

#include <cstdio>
#include "egui.h"
#include "gls.h"
#include "debug_macros.h"
#include "Shader.h"

Egui egui;
GLuint program;
GLuint a_pos;
GLuint a_srgba;
GLuint a_tc;

void void_call_handler() {
  Log("void_call_handler() called, test pass.");
}

bool before_handler() {
  glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set background color to black and opaque
  glClear(GL_COLOR_BUFFER_BIT);         // Clear the color buffer
  return true;

  // const GLfloat vertices[] = {
  //     -0.5f, 0.5f, 0.0f,       // v0
  //     1.0f, 0.0f, 0.0f,        // c0
  //     -0.5f, -0.5f, 0.0f,      // v1
  //     0.0f, 1.0f, 0.0f,        // c1
  //     0.5f, -0.5f, 0.0f,       // v2
  //     0.0f, 0.0f, 1.0f,        // c2
  //     0.5f, 0.5f, 0.0f,        // v3
  //     0.5f, 1.0f, 1.0f,        // c3
  // };
  // const GLushort indices[] = {0, 1, 2, 0, 2, 3};
  // glEnableVertexAttribArray(0);
  // glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 6 * sizeof(GLfloat), vertices);
  // glEnableVertexAttribArray(1);
  // glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 6 * sizeof(GLfloat), vertices + 3);
  // glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, indices);
  // glFlush();
  // return false;
}

void mesh_handler(float min_x, float min_y, float max_x, float max_y, const uint16_t *indices, size_t indices_len,
                  const Vertex *vertices, size_t vertices_len,
                  bool texture_managed, uint64_t texture_id) {
  glEnableVertexAttribArray(a_pos);
  glVertexAttribPointer(a_pos, 2, GL_FLOAT, GL_FALSE, sizeof(Vertex), vertices);
  glEnableVertexAttribArray(a_tc);
  glVertexAttribPointer(a_tc, 2, GL_FLOAT, GL_FALSE, sizeof(Vertex), vertices + sizeof(Pos2));
  glEnableVertexAttribArray(a_srgba);
  glVertexAttribPointer(a_srgba, 4, GL_BYTE, GL_FALSE, sizeof(Vertex), vertices + (sizeof(Pos2) << 1));

  glDrawElements(GL_TRIANGLES, (GLint) indices_len, GL_UNSIGNED_SHORT, indices);
}

void after_handler() {
  glFlush();
  glutMainLoopEvent();
}

void display() {}

int main(int argc, char **argv) {
  glutInit(&argc, argv);                 // Initialize GLUT
  glutInitDisplayMode(GLUT_RGBA | GLUT_ALPHA);
  glutCreateWindow("OpenGL Mesh Test"); // Create a window with the given title
  glutInitWindowSize(600, 600);   // Set the window's initial width & height
  glutInitWindowPosition(50, 50); // Position the window's initial top-left corner

  // disable stdio buffer
  setvbuf(stdout, nullptr, _IONBF, 0);
  printf("opengl version: %s", glGetString(GL_VERSION));

  // auto shader = Shader("egui");
  auto shader = Shader("mesh_test");
  program = shader.program;
  glUseProgram(program);

  // init args locations
  a_pos = glGetAttribLocation(program, "a_pos");
  a_tc = glGetAttribLocation(program, "a_tc");
  a_srgba = glGetAttribLocation(program, "a_srgba");

  call_void(void_call_handler);
  egui = egui_create(before_handler, mesh_handler, after_handler);

  glutDisplayFunc(display);
  egui_run_block(egui);

  // egui_run(egui);
  // glutDisplayFunc(display);
  // glutMainLoop();
  return 0;
}