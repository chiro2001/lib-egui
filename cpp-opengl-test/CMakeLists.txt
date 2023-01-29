cmake_minimum_required(VERSION 3.24)
project(cpp_opengl_test)

set(CMAKE_CXX_STANDARD 17)

find_package(OpenGL REQUIRED)
find_package(GLUT REQUIRED)
find_package(GLEW REQUIRED)
find_package(fmt REQUIRED)
include_directories(${OPENGL_INCLUDE_DIRS} ${GLUT_INCLUDE_DIRS} ${GLEW_INCLUDE_DIRS})

set(PROJECT_SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/Shader.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/utils.cpp
        )

add_executable(cpp_opengl_test main.cpp ${PROJECT_SOURCES})
add_executable(mesh_test mesh.cpp ${PROJECT_SOURCES})
add_executable(egui_test egui_test.cpp ${PROJECT_SOURCES})

set(EGUI_LIB_PATH ${CMAKE_CURRENT_SOURCE_DIR}/../target/debug/libegui.a)

set(PROJECT_LIBS OpenGL::OpenGL GLUT::GLUT GLEW::GLEW fmt::fmt ${EGUI_LIB_PATH})

target_link_libraries(cpp_opengl_test ${PROJECT_LIBS})
target_link_libraries(mesh_test ${PROJECT_LIBS})
target_link_libraries(egui_test ${PROJECT_LIBS})
