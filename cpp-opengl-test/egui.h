//
// Created by chiro on 23-1-29.
//

#ifndef EGUI_H
#define EGUI_H

#include "gls.h"

#ifdef __cplusplus

#include <cstdint>

extern "C" {
#else
#include <stdint.h>
#endif

using std::size_t;

class Pos2 {
public:
  float x;
  float y;
};

class Vertex {
public:
  Pos2 pos;
  Pos2 uv;
  uint32_t color;
};

class EguiTextureId {
public:
  uint8_t typ;
  uint64_t value;

  bool operator<(const EguiTextureId &i) const;
};
class EguiImageData {
public:
  size_t size[2];
  uint8_t *pixels;
  size_t len;
};
enum EguiTextureFilter {
  Nearest = 0,
  Linear = 1,
};
class EguiTextureOptions {
public:
  EguiTextureFilter magnification;
  EguiTextureFilter minification;

  static inline GLint code(EguiTextureFilter i) {
    if (i == Linear) return GL_LINEAR;
    else return GL_NEAREST;
  }
};
static_assert(sizeof(EguiTextureFilter) == 4);
class EguiImageDelta {
public:
  EguiImageData image;
  EguiTextureOptions option;
  size_t pos[2];
  bool pos_valid;
};

typedef
bool (*BeforeHandler)();
typedef void (*MeshHandler)(float, float, float, float, const uint16_t *, size_t, const Vertex *, size_t, bool,
                            uint64_t);
typedef void (*AfterHandler)();
typedef void (*SetTextureHandler)(const EguiTextureId *, const EguiImageDelta *);
typedef void (*FreeTextureHandler)(const EguiTextureId *);

typedef void *Egui;

void call_void(void (*)());

Egui egui_create(BeforeHandler
                 beforeHandler, MeshHandler meshHandler, AfterHandler
                 afterHandler,
                 SetTextureHandler setTextHandler, FreeTextureHandler
                 freeTextureHandler);
void egui_run(Egui egui);
void egui_run_block(Egui egui);
void egui_quit(Egui egui);


#ifdef __cplusplus
};
#endif

#endif //EGUI_H
