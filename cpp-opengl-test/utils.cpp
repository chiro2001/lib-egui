//
// Created by chiro on 23-1-29.
//

#include "utils.h"

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