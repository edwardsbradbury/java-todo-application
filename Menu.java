package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.FileDialog;
import java.io.FilenameFilter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class Menu {

  private static FileWriter fw;

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to retrieve the data from the todo currently selected in the list, format it for readability and display it in a JLabel next to the list

  public static String formatTodo(Todo selectedTodo) {

    String colour = selectedTodo.getCat().name();
    LocalDateTime isDue = selectedTodo.getDue();
    String dueDate = isDue.getDayOfWeek() + " " + isDue.getDayOfMonth() + " of " + isDue.getMonth() + " " + isDue.getYear();
    String dueTime = isDue.getHour() + ":" + isDue.getMinute() + (isDue.getMinute() < 6 ? isDue.getSecond() : "");

    String todoOutput = "<html><div bgcolor='" + colour + "'>";
    todoOutput += selectedTodo.getText();
    todoOutput += "<br>Due on: " + dueDate;
    todoOutput += "<br>Due by: " + dueTime;
    todoOutput += "<br>Importance: " + selectedTodo.getImportance();
    todoOutput += "<br>Status: " + selectedTodo.getCompletion();
    todoOutput += "</div></html>";

    return todoOutput;

  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Take a string as input & return the corresponding Category enum constant

  public static Category stringToCat(String catString) {

    return switch(catString) {
      case "red":
        yield Category.red;
      case "white":
        yield Category.white;
      case "blue":
        yield Category.blue;
      case "purple":
        yield Category.purple;
      case "yellow":
        yield Category.yellow;
      default:
        yield Category.green;
    };

  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Take a string input & return corresponding Importance enum constant

  public static Importance stringToImportance(String importanceStr) {

    return switch(importanceStr) {
      case "low":
        yield Importance.low;
      case "normal":
        yield Importance.normal;
      default:
        yield Importance.high;
    };

  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Take a string input & return corresponding Status enum constant

  public static Status stringToStatus(String statusStr) {

    return switch(statusStr) {
      case "pending":
        yield Status.pending;
      case "started":
        yield Status.started;
      case "partial":
        yield Status.partial;
      default:
        yield Status.completed;
    };

  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to sort the Todos by due date

  public static ArrayList<Todo> sortByDue(ArrayList<Todo> todos) {
    todos.sort((td1, td2) -> {
      if (td1.getDue().isAfter(td2.getDue())) {
        return 1;
      } else if (td1.getDue().isEqual(td2.getDue())) {
        return 0;
      } else {
        return -1;
      }
    });
    return todos;
  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to sort Todos by importance

  public static ArrayList<Todo> sortByPriority(ArrayList<Todo> todos) {
    todos.sort((td1, td2) -> {
      int toReturn;
      if (td1.getImportance().ordinal() > td2.getImportance().ordinal()) {
        toReturn = 1;
      } else if (td1.getImportance().ordinal() == td2.getImportance().ordinal()) {
        toReturn = 0;
      } else {
        toReturn = -1;
      }
      return toReturn;
    });
    return todos;
  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to sort Todos by completion status

  public static ArrayList<Todo> sortByStatus(ArrayList<Todo> todos) {
    todos.sort((td1, td2) -> /*td1.getCompletion().ordinal() <= td2.getCompletion().ordinal() ? -1 : 1); */ {
      int toReturn;
      if (td1.getCompletion().ordinal() > td2.getCompletion().ordinal()) {
        toReturn = 1;
      } else if (td1.getCompletion().ordinal() == td2.getCompletion().ordinal()) {
        toReturn = 0;
      } else {
        toReturn = -1;
      }
      return toReturn;
    });
    return todos;
  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to load Todos from a file on user's machine

  public static ArrayList<Todo> loadFromFile(FileDialog fileBrowser) {

    fileBrowser.setMultipleMode(false);

    fileBrowser.setFilenameFilter(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".txt");
      }
    });

    fileBrowser.setVisible(true);

    String directory = fileBrowser.getDirectory();
    String filename = fileBrowser.getFile();

    ArrayList<Todo> todos = new ArrayList<>();

    if (filename != null) {

      try {

        File toLoad = new File(directory + filename);
        Scanner in = new Scanner(toLoad);

        while (in.hasNextLine()) {

          String toDoString = in.nextLine();
          String[] splitStrings = toDoString.split(",");
          LocalDateTime due = LocalDateTime.parse(splitStrings[1]);
          Category cat = Menu.stringToCat(splitStrings[2]);
          Importance priority = Menu.stringToImportance(splitStrings[3]);
          Status completion = Menu.stringToStatus(splitStrings[4]);

          Todo toInsert = new Todo(splitStrings[0], due, cat, priority, completion);
          todos.add(toInsert);

        }

      } catch (FileNotFoundException exc) {
        System.out.println("File not found");
      }
    }
    return todos;
  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to save the data from the Todos arrayList to an external file

  public static void saveToFile(FileDialog fileBrowser, ArrayList<Todo> todos) throws IOException {

    fileBrowser.setMultipleMode(false);

    fileBrowser.setVisible(true);

    String directory = fileBrowser.getDirectory();
    String fileName = fileBrowser.getFile();

    if (directory != null && fileName != null) {

      File file = new File(directory + fileName + ".txt");
      fw = new FileWriter(file);

      todos.forEach(aTodo -> {
        String todoString = aTodo.getText();
        todoString += "," + aTodo.getDue();
        todoString += "," + aTodo.getCat().name();
        todoString += "," + aTodo.getImportance().name();
        todoString += "," + aTodo.getCompletion().name() + "\n";
        try {
          fw.append(todoString);
        } catch (IOException e) {
          e.printStackTrace();
        }
      });

      fw.close();
    }

  }

}
