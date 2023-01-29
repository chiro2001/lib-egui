cmake_minimum_required(VERSION 3.24)
project(cpp_opengl_test)

set(CMAKE_CXX_STANDARD 17)

find_package(OpenGL REQUIRED)
find_package(GLUT REQUIRED)
find_package(GLEW REQUIRED)
find_package(fmt REQUIRED)
include_directories(${OPENGL_INCLUDE_DIRS} ${GLUT_INCLUDE_DIRS} ${GLEW_INCLUDE_DIRS})

set(CPM_DOWNLOAD_VERSION 0.36.0)

if (CPM_SOURCE_CACHE)
    set(CPM_DOWNLOAD_LOCATION "${CPM_SOURCE_CACHE}/cpm/CPM_${CPM_DOWNLOAD_VERSION}.cmake")
elseif (DEFINED ENV{CPM_SOURCE_CACHE})
    set(CPM_DOWNLOAD_LOCATION "$ENV{CPM_SOURCE_CACHE}/cpm/CPM_${CPM_DOWNLOAD_VERSION}.cmake")
else ()
    set(CPM_DOWNLOAD_LOCATION "${CMAKE_BINARY_DIR}/cmake/CPM_${CPM_DOWNLOAD_VERSION}.cmake")
endif ()

# Expand relative path. This is important if the provided path contains a tilde (~)
get_filename_component(CPM_DOWNLOAD_LOCATION ${CPM_DOWNLOAD_LOCATION} ABSOLUTE)
if (NOT (EXISTS ${CPM_DOWNLOAD_LOCATION}))
    message(STATUS "Downloading CPM.cmake to ${CPM_DOWNLOAD_LOCATION}")
    file(DOWNLOAD
            https://github.com/cpm-cmake/CPM.cmake/releases/download/v${CPM_DOWNLOAD_VERSION}/CPM.cmake
            ${CPM_DOWNLOAD_LOCATION}
            )
endif ()

include(${CPM_DOWNLOAD_LOCATION})

CPMAddPackage(
        NAME debug_macros
        VERSION 1.03
        GITHUB_REPOSITORY chiro2001/debug-macros)

link_libraries(debug_macros)

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
