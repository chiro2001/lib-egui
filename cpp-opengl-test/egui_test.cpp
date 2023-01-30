//
// Created by chiro on 23-1-29.
//

#include <cstdio>
#include <chrono>
#include <map>
#include <fmt/format.h>
#include "egui.h"
#include "gls.h"
#include "debug_macros.h"
#include "Shader.h"

Egui egui;
GLuint program;
GLint a_pos;
GLint a_srgba;
GLint a_tc;
int screen_width = 640;
int screen_height = 480;

void void_call_handler() {
  Log("void_call_handler() called, test pass.");
}

void upload_texture_srgb(
    const size_t *pos,
    const size_t *rect,
    const EguiTextureOptions *options,
    const uint8_t *data,
    const size_t length) {
}

bool before_handler() {
  glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set background color to black and opaque
  // glClearColor(0.2f, 0.3f, 0.4f, 1.0f); // Set background color to black and opaque
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
  auto static start = std::chrono::system_clock::now();
  auto now = std::chrono::system_clock::now();
  static uint32_t frame_count = 0;
  frame_count++;
  auto duration_ms = std::chrono::duration_cast<std::chrono::milliseconds>(now - start).count();
  if (duration_ms > 1000) {
    start = now;
    glutSetWindowTitle(fmt::format("FPS: {}", frame_count).c_str());
    frame_count = 0;
  }
}

std::map<EguiTextureId, GLuint> textures = {};

void set_texture(const EguiTextureId *id, const EguiImageDelta *delta) {
  GLuint texture;
  if (textures.find(*id) != textures.end()) {
    texture = textures.at(*id);
  } else {
    glGenTextures(1, &texture);
    textures.insert(std::pair(*id, texture));
  }
  glBindTexture(GL_TEXTURE_2D, texture);
  upload_texture_srgb(delta->pos, delta->image.size, &delta->option, delta->image.pixels, delta->image.len);
}

void free_texture(const EguiTextureId *id) {}

void display() {}

int main(int argc, char **argv) {
  glutInit(&argc, argv);                 // Initialize GLUT
  glutInitDisplayMode(GLUT_RGBA | GLUT_ALPHA);
  glutInitWindowSize(screen_width, screen_height);   // Set the window's initial width & height
  // glutInitWindowPosition(50, 50); // Position the window's initial top-left corner
  glutCreateWindow("OpenGL Mesh Test"); // Create a window with the given title

  // disable stdio buffer
  setvbuf(stdout, nullptr, _IONBF, 0);
  Log("opengl version: %s", glGetString(GL_VERSION));
  Log("sizeof(Vertex) = %lu", sizeof(Vertex));

  // auto shader = Shader("egui_150");
  auto shader = Shader("glow");
  // auto shader = Shader("egui");
  // auto shader = Shader("mesh_test");
  program = shader.program;
  // glUseProgram(program);

  // init args locations
  a_pos = glGetAttribLocation(program, "a_pos");
  a_tc = glGetAttribLocation(program, "a_tc");
  a_srgba = glGetAttribLocation(program, "a_srgba");
  Assert(a_pos >= 0, "Cannot locate a_pos");
  Assert(a_tc >= 0, "Cannot locate a_tc");
  Assert(a_srgba >= 0, "Cannot locate a_srgba");

  // init screen size
  GLint screen_size = glGetUniformLocation(program, "u_screen_size");
  Assert(screen_size >= 0, "Cannot locate screen_size");
  glUniform2f(screen_size, (float) (screen_width), (float) (screen_height));
  GLint sampler = glGetUniformLocation(program, "u_sampler");
  Assert(sampler >= 0, "Cannot locate sampler");
  glUniform1i(sampler, 0);
  glViewport(0, 0, screen_width, screen_height);

  call_void(void_call_handler);
  egui = egui_create(before_handler, mesh_handler, after_handler, set_texture, free_texture);

  glutDisplayFunc(display);
  egui_run_block(egui);

  // egui_run(egui);
  // glutDisplayFunc(display);
  // glutMainLoop();
  return 0;
}