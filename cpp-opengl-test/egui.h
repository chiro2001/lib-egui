//
// Created by chiro on 23-1-29.
//

#ifndef EGUI_H
#define EGUI_H

#ifdef __cplusplus

#include <cstdint>

extern "C" {
#else
#include <stdint.h>
#endif

typedef struct {
  float x;
  float y;
} Pos2;

typedef struct {
  Pos2 pos;
  Pos2 uv;
  uint32_t color;
} Vertex;

enum TextureFilter {
  Nearest = 0,
  Linear = 1,
};

typedef struct {
  TextureFilter magnification;
  TextureFilter minification;
} TextureOptions;

static_assert(sizeof(TextureFilter) == 4);

typedef bool (*BeforeHandler)();
typedef void (*MeshHandler)(float, float, float, float, const uint16_t *, size_t, const Vertex *, size_t, bool,
                            uint64_t);
typedef void (*AfterHandler)();

typedef void *Egui;

void call_void(void (*)());

Egui egui_create(BeforeHandler beforeHandler, MeshHandler meshHandler, AfterHandler afterHandler);
void egui_run(Egui egui);
void egui_run_block(Egui egui);
void egui_quit(Egui egui);


#ifdef __cplusplus
};
#endif

#endif //EGUI_H
