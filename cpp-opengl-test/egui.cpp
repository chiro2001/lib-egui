//
// Created by chiro on 23-1-30.
//
#include "egui.h"

bool EguiTextureId::operator<(const EguiTextureId &i) const {
  return this->typ == i.typ ? this->value < i.value : this->typ < i.typ;
}
