//
// Created by chiro on 23-1-29.
//

#ifndef CPP_OPENGL_TEST_SHADER_H
#define CPP_OPENGL_TEST_SHADER_H

#include <string>
#include "gls.h"

class Shader {
public:
  GLuint program;

  explicit Shader(const char *name);

  static GLuint compile(const std::string &path, int typ);

  static GLuint link(GLuint vs, GLuint fs);
};


#endif //CPP_OPENGL_TEST_SHADER_H
