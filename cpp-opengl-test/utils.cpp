//
// Created by chiro on 23-1-29.
//

#include "utils.h"
#include "debug_macros.h"
#include "gls.h"

// two header files iostream and fstream is included to enable us to use cout and ifstream. #include <iostream>
#include <fstream>

using namespace std;

//defining the file exists function which checks if a file exists or not and returns one if file exists and returns 0 if file do not exist
bool file_exists(const string &filename) {
  ifstream file(filename);
  if (file.is_open()) {
    file.close();
    return true;
  } else {
    return false;
  }
}

void report_gl_error(const char *msg) {
  GLenum e = glGetError();
  if (e != GL_NO_ERROR) {
    const char *s;
    switch (e) {
      case GL_INVALID_ENUM:
        s = "GL_INVALID_ENUM";
        break;
      case GL_INVALID_VALUE:
        s = "GL_INVALID_VALUE";
        break;
      case GL_INVALID_OPERATION:
        s = "GL_INVALID_OPERATION";
        break;
      case GL_STACK_OVERFLOW:
        s = "GL_STACK_OVERFLOW";
        break;
      case GL_STACK_UNDERFLOW:
        s = "GL_STACK_UNDERFLOW";
        break;
      case GL_OUT_OF_MEMORY:
        s = "GL_OUT_OF_MEMORY";
        break;
      case GL_INVALID_FRAMEBUFFER_OPERATION:
        s = "GL_INVALID_FRAMEBUFFER_OPERATION";
        break;
      case GL_CONTEXT_LOST:
        s = "GL_CONTEXT_LOST";
        break;
      case GL_TABLE_TOO_LARGE:
        s = "GL_TABLE_TOO_LARGE";
        break;
      default:
        s = "<unknown>";
        break;
    }
    Err("GL error at %s 0x%X: %s", msg, e, s);
  }
}
