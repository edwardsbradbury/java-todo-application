package com.company;

// Enum for the choices of category which can be attributed to an instance of Todo
public enum Category {

  red("\033[0;31m"), white("\033[0;37m"), blue("\033[0;34m"), purple("\033[0;35m"), yellow("\033[0;33m"), green("\033[0;32m");

  private String colour;

  Category(String value) {
    colour = value;
  }

  public String getColour() {
    return colour;
  }

}
