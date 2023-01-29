cmake_minimum_required(VERSION 3.24)
project(cpp_opengl_test)

set(CMAKE_CXX_STANDARD 17)

find_package(OpenGL REQUIRED)
find_package(GLUT REQUIRED)
include_directories(${OPENGL_INCLUDE_DIRS} ${GLUT_INCLUDE_DIRS})

add_executable(cpp_opengl_test main.cpp)
add_executable(mesh_test mesh.cpp)

target_link_libraries(cpp_opengl_test OpenGL::OpenGL GLUT::GLUT)
target_link_libraries(mesh_test OpenGL::OpenGL GLUT::GLUT)
