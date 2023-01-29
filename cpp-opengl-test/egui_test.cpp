//
// Created by chiro on 23-1-29.
//

#include <cstdio>
#include "egui.h"
#include "gls.h"
#include "debug_macros.h"

void void_call_handler() {
  Log("void_call_handler() called, test pass.");
}

int main(int argc, char **argv) {
  call_void(void_call_handler);
  return 0;
}