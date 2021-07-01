package com.company;

import javax.swing.*;
import java.util.ArrayList;

public class Main {

    // Instantiate the GUI

    public static void main(String[] args) {

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {

          ArrayList<Todo> todos = new ArrayList<>();

          new GUI(todos);
        }
      });

//      new CLIMenu(new ArrayList<>());
    }
}
