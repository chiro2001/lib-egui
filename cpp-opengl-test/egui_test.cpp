//
// Created by chiro on 23-1-29.
//

#include <cstdio>
#include "egui.h"
#include "gls.h"
#include "debug_macros.h"

Egui egui;

void void_call_handler() {
  Log("void_call_handler() called, test pass.");
}

bool before_handler() {
  glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Set background color to black and opaque
  glClear(GL_COLOR_BUFFER_BIT);         // Clear the color buffer
  return true;
}

void mesh_handler(float min_x, float min_y, float max_x, float max_y, const uint16_t *indices, size_t indices_len,
                  const Vertex *vertices, size_t vertices_len,
                  bool texture_managed, uint64_t texture_id) {
}

void after_handler() {}

int main(int argc, char **argv) {
  call_void(void_call_handler);
  egui = egui_create(before_handler, mesh_handler, after_handler);
  // egui_run(egui);
  egui_run_block(egui);
  return 0;
}