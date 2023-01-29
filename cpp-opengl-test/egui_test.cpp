//
// Created by chiro on 23-1-29.
//

#include <cstdio>
#include "egui.h"

void void_call_handler() {
  printf("void_call_handler() called");
}

int main(int argc, char **argv) {
  call_void(void_call_handler);
  return 0;
}