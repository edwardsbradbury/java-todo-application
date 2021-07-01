package com.company;

import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/* Class for a menu UI at the command line, with methods to print all todo instances, create a new todo instance,
  update a todo and delete a todo */
public class CLIMenu {

  // Class instance variable
  private ArrayList<Todo> todos;


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to take and validate integer input for menus where selection is integer based

  private int getIntInput(Scanner scan, String options, int maxOption) {

    int option = 0;
    boolean optionValid = false;

    do {
      System.out.println(options);
      while (!scan.hasNextInt()) {
        System.out.println("\nOption not recognised");
        System.out.println(options);
        scan.next();
      }
      option = scan.nextInt();
      if (option < 1 || option > maxOption) {
        System.out.println("\nOption not recognised");
      } else {
        optionValid = true;
      }
    } while (!optionValid);

    return option;
  }


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /* Method to print each todo instance in the todos arraylist, if it contains todos. Otherwise, prints a message
      that the user hasn't created any todos */

  private void listAll() {
    if (todos.size() < 1) {
      System.out.println("\nYou haven't made any todos yet");
    } else {
      System.out.println("\nYour current Todos are:");
      todos.forEach(aTodo -> System.out.println("\n" + (todos.indexOf(aTodo) + 1) + ": " + aTodo));
    }
  }


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to listen for & validate input for the title of a Todo. Used in addTodo & updateTodo methods

  private String getTitle(Scanner scan) {

    String title;
    boolean titleValid = false;

    /* Prompt the user to input a title for their Todo. Keep prompting user & checking for input until the input is 1-30
      characters long, containing only letters, digits, spaces and apostrophes */
    do {
      System.out.println("\nEnter a title for your Todo:\n");
      while (!scan.hasNext()) {
        scan.next();
      }
      title = scan.nextLine().trim();
      if (title.matches("[A-Za-z0-9 ']{1,30}") && !(title.equals("'"))) {
        titleValid = true;
      } else if (title.length() > 0) {
        System.out.println("\nTitle must be 1-30 characters long, containing only letters, digits, spaces and apostrophes.\nTitle cannot be a single apostrophe\n");
      }
    } while (!titleValid);
    return title;
  }


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to lsiten for & validate a date for the Todo's due date. Used in addTodo & updateTodo methods

  private LocalDateTime getDue(Scanner scan, String mode) {

    LocalDateTime dueDate = null;
    Boolean dueDateValid = false;

    /* Prompt the user to input a title for their Todo. Keep prompting user & checking for input until a valid date is entered
      (valid if in the specified format and after the current moment) */
    do {
      String dateInputPrompt = "\nEnter the date that your todo should be done by, in the format YYYY-MM-DDTHH:MM\n";
      System.out.println(dateInputPrompt);
      while (!scan.hasNext()) {
        System.out.println(dateInputPrompt);
        scan.next();
      }
      String dateInput = scan.nextLine();
      try {
        dueDate = LocalDateTime.parse(dateInput);
        if (mode.equals("new")) {
          dueDateValid = dueDate.isAfter(LocalDateTime.now());
        } else {
          dueDateValid = dueDate.isAfter(LocalDateTime.parse("2021-05-06T09:00"));
        }
        if (!dueDateValid) {
          System.out.println(mode.equals("new") ? "\nYour Todo can't be due for completion in the past!\n" : "Your Todo can't be due before this app was released!");
        }
      } catch (DateTimeParseException exc) {
        System.out.println("\nThe due date you entered is not valid\n");
      }
    } while (!dueDateValid);
    return dueDate;
  }


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to listen for & validate the selection of a category to attribute to a Todo

  private Category getCat(Scanner scan) {

    String catPrompt = "\nSelect a category:\n1: Red\n2: White\n3: Blue\n4: Purple\n5: Yellow\n6: Green\n";
    int catSelection = 0;
    Category cat = null;

    catSelection = getIntInput(scan, catPrompt, 6);

    cat = switch (catSelection) {
      case 1:
        yield Category.red;
      case 2:
        yield Category.white;
      case 3:
        yield Category.blue;
      case 4:
        yield Category.purple;
      case 5:
        yield Category.yellow;
      default:
        yield Category.green;
    };

    return cat;
  }


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to get user's choice of Importance

  private Importance getImportance(Scanner scan) {

    String priorityPrompt = "\nSelect a priority level:\n1: Low\n2: Normal\n3: High\n";
    int prioritySelection = 0;
    Importance priority = null;

    prioritySelection = getIntInput(scan, priorityPrompt, 3);

    priority = switch (prioritySelection) {
      case 1:
        yield Importance.low;
      case 2:
        yield Importance.normal;
      default:
        yield Importance.high;
    };

    return priority;
  }


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to get user's choice of a new completion status, for use in updateTodo method below

  private Status getStatus(Scanner scan) {

    String statusPrompt = "\nSelect the new status for your todo:\n1) Pending\n2) Started\n3) Partial\n4)Completed\n";
    int statusSelection = 0;
    Status newStatus = null;

    statusSelection = getIntInput(scan, statusPrompt, 4);

    newStatus = switch (statusSelection) {
      case 1:
        yield Status.pending;
      case 2:
        yield Status.started;
      case 3:
        yield Status.partial;
      default:
        yield Status.completed;
    };

    return newStatus;
  }


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /* Method to create a new instance of Todo from user input and add it to the todos arraylist uses the methods defined above
    to take & validate input for each property of the Todo */

  private void addTodo(Scanner scan) {

    String title;
    LocalDateTime dueDate;
    Category cat;
    Importance priority;

    title = getTitle(scan);

    dueDate = getDue(scan, "new");

    cat = getCat(scan);

    priority = getImportance(scan);

    Todo newTodo = new Todo(title, dueDate, cat, priority, Status.pending);
    this.todos.add(newTodo);
    System.out.println("\nCreated new todo:\n" + newTodo);

  }


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to get the index - of a Todo in todos array - that user wants to update or delete

  private int getIndex(Scanner scan, String mode) {

    String selectionPrompt = "\nEnter the number of the Todo you want to " + mode + ":\n";
    int index = 0;

    if (todos.size() < 1) {
      System.out.println("\nYou can't " + mode + " a todo if you don't have any!");
      return -1;
    }

    listAll();
    index = getIntInput(scan, selectionPrompt, todos.size());

    return --index;
  }


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to update an instance of Todo

  private void updateTodo(Scanner scan) {

    String fieldPrompt = "\nWhich field of your todo do you want to update:\n1) Title\n2) Due by\n3) Category\n4) Importance \n5) Completion status";
    int fieldToUpdate = 0;

    int indexToUpdate = getIndex(scan, "update");
    if (indexToUpdate == -1) {
      return;
    }

    fieldToUpdate = getIntInput(scan, fieldPrompt, 5);

    switch (fieldToUpdate) {
      case 1:
        String newTitle = getTitle(scan);
        todos.get(indexToUpdate).setText(newTitle);
        break;
      case 2:
        LocalDateTime newDueDate = getDue(scan, "update");
        todos.get(indexToUpdate).setDue(newDueDate);
        break;
      case 3:
        Category newCat = getCat(scan);
        todos.get(indexToUpdate).setCat(newCat);
        break;
      case 4:
        Importance newPriority = getImportance(scan);
        todos.get(indexToUpdate).setImportance(newPriority);
        break;
      default:
        Status newStatus = getStatus(scan);
        todos.get(indexToUpdate).setCompletion(newStatus);
        break;
    };

    System.out.println("\nYour todo was updated:\n\n" + todos.get(indexToUpdate));

  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to delete a Todo

  private void deleteTodo(Scanner scan) {

    int toDelete = getIndex(scan, "delete");

    if (toDelete == -1) {
      return;
    }

    todos.remove(toDelete);

    System.out.println("\nTodo #" + (toDelete + 1) + " was deleted");
    listAll();

  }


  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  /* Constructor for main menu UI. Prompts the user to input an int 1-5 to choose an option. Keeps prompting & checking for new input
    until user enters 5 to quit */

  CLIMenu(ArrayList<Todo> todos) {

    this.todos = todos;
    String menuOptions = "\nSelect an option by typing the corresponding number:\n1) List all 'todo' notes\n2) Create a new 'todo'\n3) Update a todo \n4) Delete a todo\n5) Quit";
    int option = 0;
    Scanner in = new Scanner(System.in);

    do {

      option = getIntInput(in, menuOptions, 5);

      switch (option) {
        case 1:
          this.listAll();
          break;
        case 2:
          this.addTodo(in);
          break;
        case 3:
          this.updateTodo(in);
          break;
        case 4:
          this.deleteTodo(in);
          break;
      }

    } while (option != 5);

    System.out.println("Goodbye");

  }

}
