//
// Created by chiro on 23-1-29.
//

#include "Shader.h"
#include <fmt/format.h>
#include <fstream>
#include <sstream>
#include "utils.h"
#include "gls.h"

Shader::Shader(const char *name) {
  std::string path_vert = fmt::format("../shaders/{}.vert", name);
  std::string path_frag = fmt::format("../shaders/{}.frag", name);
  if (!file_exists(path_vert) || !file_exists(path_frag)) {
    path_vert = fmt::format("shaders/{}.vert", name);
    path_frag = fmt::format("shaders/{}.frag", name);
    if (!file_exists(path_vert) || !file_exists(path_frag))
      throw std::runtime_error("file not found");
  }
  GLuint vert = Shader::compile(path_vert, GL_VERTEX_SHADER);
  GLuint frag = Shader::compile(path_frag, GL_FRAGMENT_SHADER);
  this->program = Shader::link(vert, frag);
}

GLuint Shader::compile(const std::string &path, int typ) {
  GLuint shader = glCreateShader(typ);
  std::ifstream file(path, std::ios::in);
  if (!file.is_open()) throw std::runtime_error("file not found");
  std::ostringstream stream;
  stream << file.rdbuf();
  std::string content = stream.str();
  glShaderSource(shader, 1, reinterpret_cast<const GLchar *const *>(content.c_str()), nullptr);
  glCompileShader(shader);
  int status;
  glGetShaderiv(shader, GL_COMPILE_STATUS, &status);
  if (status != GL_TRUE) {
    const GLsizei buf_size = 1024;
    GLsizei length = 0;
    GLchar buf[buf_size];
    glGetShaderInfoLog(shader, buf_size, &length, buf);
    buf[length] = '\0';
    throw std::runtime_error(buf);
  }
  return shader;
}

GLuint Shader::link(GLuint vs, GLuint fs) {
  GLuint program = glCreateProgram();
  glAttachShader(program, vs);
  glAttachShader(program, fs);
  glLinkProgram(program);
  int status;
  glGetProgramiv(program, GL_LINK_STATUS, &status);
  if (status != GL_TRUE) {
    const GLsizei buf_size = 1024;
    GLsizei length = 0;
    GLchar buf[buf_size];
    glGetProgramInfoLog(program, buf_size, &length, buf);
    buf[length] = '\0';
    throw std::runtime_error(buf);
  }
  return program;
}
