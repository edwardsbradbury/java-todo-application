/* GUI with methods to handle user interactions such as creating/updating/deleting a todo note,
  loading todo notes from a .txt file and exporting your todo notes out to a .txt file.

  Unfortunately, some of the business logic (which should be separated out in Menu class) is in this class with
  the GUI logic, because I didn't quite figure out how I could access & modify GUI's instance variables from
  Menu.java. Tried public access modifiers but couldn't figure it out, perhaps should have tried static members.
  Updating instance variables (e.g. JLabels) is necessary for providing feedback to user. */

package com.company;

// Dependencies
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

// Extending JFrame and calling super() in the GUI constructor creates the JFrame instance
public class GUI extends JFrame implements ListSelectionListener, ActionListener {

  // Instance variables
  private ArrayList<Todo> todos;
  private DefaultListModel<String> todosListModel;
  private JList<String> todosJList;
  private int todoIndex;
  private int width;
  private int height;
  private JLabel heading;
  private JLabel todoData;
  private JButton addButton;
  private JButton updateButton;
  private JButton deleteButton;
  private JPanel defaultView;
  private JComboBox sortBy;
  private JPanel newTodoPanel;
  private JLabel titleLabel;
  private JTextField title;
  private JLabel dueLabel;
  private JTextField dueBy;
  private JLabel catLabel;
  private String[] categories;
  private JComboBox catDropdown;
  private JLabel priorityLabel;
  private String[] priorities;
  private JComboBox priorityDropdown;
  private JButton cancelButton;
  private JButton createButton;
  private JPanel updatePanel;
  private JComboBox statusDropdown;
  private JButton saveButton;
  private JButton loadButton;
  public FileDialog fileBrowser;

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Constructor. Initializes instance variables, creates and configures components, inserts components into frame & displays the frame

  public GUI(ArrayList<Todo> allTodos) {

    super();

    todos = Menu.sortByDue(allTodos);
    todoIndex = -1;
    categories = new String[]{"red", "white", "blue", "purple", "yellow", "green"};
    priorities = new String[]{"low", "normal", "high"};
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    width = screensize.width;
    height = screensize.height;
    setSize(new Dimension(width / 3, height / 3));
    setLocation(width / 3, 25);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("Todo list");
    defaultView();
    setVisible(true);

  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Listen for list change events & run methods defined above depending on which JList the selection change event emanated from

  public void valueChanged(ListSelectionEvent event) {

    if (event.getSource() == todosJList) {
      todoIndex = todosJList.getSelectedIndex();
      if (todoIndex == -1) {
        heading.setText(todos.size() > 0 ? "Select a todo" : "You haven't made any Todos yet");
      } else {
        updateButton.setVisible(true);
        deleteButton.setVisible(true);
        displayTodo(todos.get(todoIndex));
      }
    }

  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Button click event listeners to run methods declared above, depending on which button triggered the event

  public void actionPerformed(ActionEvent event) {

    Object trigger = event.getSource();

    if (trigger == addButton) {
      remove(defaultView);
      createTodoPanel();
    } else if (trigger == updateButton) {
      remove(defaultView);
      updatePanel();
    } else if (trigger == deleteButton) {
      deleteTodo();
    } else if (trigger == cancelButton) {
      todoIndex = -1;
      remove(event.getActionCommand().equals("Cancel insert") ? newTodoPanel : updatePanel);
      todos = Menu.sortByDue(todos);
      defaultView();
      repaint();
      revalidate();
    } else if (trigger == createButton) {
      if (event.getActionCommand().equals("Create")) {
        createTodo();
      } else {
        updateTodo();
      }
    } else if (trigger == sortBy) {
      switch ((String) sortBy.getSelectedItem()) {
        case "due":
          todos = Menu.sortByDue(todos);
          applySorting();
        case "priority":
          todos = Menu.sortByPriority(todos);
          applySorting();
        default:
          todos = Menu.sortByStatus(todos);
          applySorting();
      }
    } else if (trigger == loadButton) {
      fileBrowser = new FileDialog(GUI.this, "Load Todos from file", FileDialog.LOAD);
      todos = Menu.loadFromFile(fileBrowser);
      remove(defaultView);
      defaultView();
      repaint();
      revalidate();
    } else if (trigger == saveButton) {
      fileBrowser = new FileDialog(GUI.this, "Save your Todos to a file", FileDialog.SAVE);
      try {
        Menu.saveToFile(fileBrowser, todos);
      } catch (IOException e) {
        heading.setText("Writing to external file failed!");
      }
    }
  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to initialize or update the list of titles of current todos

  private void populateJList() {

    todosListModel = new DefaultListModel<>();
    todos.forEach(aTodo -> todosListModel.addElement(aTodo.getText()));
    todosJList = new JList<>(todosListModel);
    todosJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    todosJList.addListSelectionListener(this);

  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to create the UI for default view of the application

  public void defaultView() {

    populateJList();
    defaultView = new JPanel(new GridBagLayout());
    defaultView.setPreferredSize(new Dimension(width / 3, height / 3));
    GridBagConstraints defaultCons = new GridBagConstraints();

    heading = new JLabel(todos.size() > 0 ? "Select a todo" : "You haven't made any Todos yet", JLabel.CENTER);
    defaultCons.gridx = 0;
    defaultCons.gridy = 0;
    defaultCons.gridwidth = 3;
    defaultCons.weightx = 0.5;
    defaultCons.anchor = GridBagConstraints.CENTER;
    defaultView.add(heading, defaultCons);

    JScrollPane scrollTodos = new JScrollPane(todosJList);
//    scrollTodos.setPreferredSize(new Dimension(500, 300));
    defaultCons.gridx = 0;
    defaultCons.gridy = 1;
    defaultCons.gridwidth = 2;
    defaultCons.anchor = GridBagConstraints.WEST;
    defaultCons.fill = GridBagConstraints.HORIZONTAL;
    defaultCons.fill = GridBagConstraints.VERTICAL;
    defaultCons.weightx = 1;
    defaultCons.weighty = 1;
    defaultView.add(scrollTodos, defaultCons);

    todoData = new JLabel("", JLabel.CENTER);
    defaultCons.gridx = 2;
    defaultCons.gridy = 1;
    defaultCons.gridwidth = 1;
    defaultCons.fill = GridBagConstraints.HORIZONTAL;
    defaultView.add(todoData, defaultCons);

    JLabel sortByLabel = new JLabel("Sort by:", JLabel.RIGHT);
    defaultCons.gridx = 0;
    defaultCons.gridy = 2;
    defaultCons.gridwidth = 1;
    defaultCons.weightx = 0;
    defaultCons.weighty = 0;
    defaultCons.anchor = GridBagConstraints.WEST;
    defaultView.add(sortByLabel, defaultCons);

    String[] sortByOptions = {"due", "priority", "status"};
    sortBy = new JComboBox(sortByOptions);
    defaultCons.gridx = 1;
    defaultCons.gridy = 2;
    defaultCons.gridwidth = 1;
    defaultCons.weightx = 0;
    defaultCons.weighty = 0;
    defaultCons.anchor = GridBagConstraints.EAST;
    sortBy.addActionListener(this);
    defaultView.add(sortBy, defaultCons);

    loadButton = new JButton("Load file");
    defaultCons.gridx = 2;
    defaultCons.gridy = 2;
    defaultCons.gridwidth = 1;
    defaultCons.weightx = 0;
    defaultCons.weighty = 0;
    defaultCons.anchor = GridBagConstraints.CENTER;
    loadButton.addActionListener(this);
    defaultView.add(loadButton, defaultCons);

    addButton = new JButton("New Todo");
    defaultCons.gridx = 0;
    defaultCons.gridy = 3;
    defaultCons.gridwidth = 1;
    defaultCons.weightx = 0;
    defaultCons.weighty = 0;
    addButton.addActionListener(this);
    defaultView.add(addButton, defaultCons);

    updateButton = new JButton("Update");
    defaultCons.gridx = 1;
    defaultCons.gridy = 3;
    defaultCons.gridwidth = 1;
    defaultCons.weightx = 0;
    defaultCons.weighty = 0;
    updateButton.addActionListener(this);
    updateButton.setVisible(false);
    defaultView.add(updateButton, defaultCons);

    deleteButton = new JButton("Delete");
    defaultCons.gridx = 2;
    defaultCons.gridy = 3;
    defaultCons.gridwidth = 1;
    defaultCons.weightx = 0;
    defaultCons.weighty = 0;
    deleteButton.addActionListener(this);
    deleteButton.setVisible(false);
    defaultView.add(deleteButton, defaultCons);

    if (todos.size() > 0) {

      saveButton = new JButton("Save");
      defaultCons.gridx = 3;
      defaultCons.gridy = 2;
      defaultCons.gridwidth = 1;
      defaultCons.weightx = 0;
      defaultCons.weighty = 0;
      saveButton.addActionListener(this);
      defaultView.add(saveButton, defaultCons);

    }

    add(defaultView);

  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Re-render the default screen to reflect re-ordering (sorting) of the todos arraylist, after using Menu.sortBy methods

  private void applySorting() {
    remove(defaultView);
    defaultView();
    repaint();
    revalidate();
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to retrieve the data from the todo currently selected in the list, format it for readability and display it in a JLabel next to the list

  private void displayTodo(Todo toBeDisplayed) {

    String colour = toBeDisplayed.getCat().name();
    todoData.setForeground(colour.equals("white") || colour.equals("yellow") ? Color.black : Color.white);
    todoData.setText(Menu.formatTodo(toBeDisplayed));

  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Create the components & layout for the new todo entry form, add it to the frame, repaint & revalidate the frame

  private void createTodoPanel() {

    newTodoPanel = new JPanel(new GridBagLayout());
    newTodoPanel.setPreferredSize(new Dimension(width / 3, height / 3));
    GridBagConstraints newTodoCons = new GridBagConstraints();

    heading = new JLabel("Create a new Todo");
    newTodoCons.gridx = 0;
    newTodoCons.gridy = 0;
    newTodoCons.gridwidth = 2;
    newTodoCons.weighty = 0.75;
    newTodoCons.anchor = GridBagConstraints.CENTER;
    newTodoPanel.add(heading, newTodoCons);

    titleLabel = new JLabel("Title:");
    newTodoCons.gridx = 0;
    newTodoCons.gridy = 1;
    newTodoCons.weighty = 0.5;
    newTodoCons.anchor = GridBagConstraints.WEST;
    newTodoPanel.add(titleLabel, newTodoCons);

    title = new JTextField(30);
    newTodoCons.gridx = 1;
    newTodoCons.gridy = 1;
    newTodoCons.anchor = GridBagConstraints.EAST;
    newTodoPanel.add(title, newTodoCons);

    dueLabel = new JLabel("<html>Due by <br>(YYYY-MM-DDTHH:MM):</html>");
    newTodoCons.gridx = 0;
    newTodoCons.gridy = 2;
    newTodoCons.anchor = GridBagConstraints.WEST;
    newTodoPanel.add(dueLabel, newTodoCons);

    dueBy = new JTextField(16);
    newTodoCons.gridx = 1;
    newTodoCons.gridy = 2;
    newTodoCons.anchor = GridBagConstraints.EAST;
    newTodoPanel.add(dueBy, newTodoCons);

    catLabel = new JLabel("Category:");
    newTodoCons.gridx = 0;
    newTodoCons.gridy = 3;
    newTodoCons.anchor = GridBagConstraints.WEST;
    newTodoPanel.add(catLabel, newTodoCons);

    catDropdown = new JComboBox(categories);
    newTodoCons.gridx = 1;
    newTodoCons.gridy = 3;
    newTodoCons.anchor = GridBagConstraints.EAST;
    newTodoPanel.add(catDropdown, newTodoCons);

    priorityLabel = new JLabel("Priority:");
    newTodoCons.gridx = 0;
    newTodoCons.gridy = 4;
    newTodoCons.anchor = GridBagConstraints.WEST;
    newTodoPanel.add(priorityLabel, newTodoCons);

    priorityDropdown = new JComboBox(priorities);
    newTodoCons.gridx = 1;
    newTodoCons.gridy = 4;
    newTodoCons.anchor = GridBagConstraints.EAST;
    newTodoPanel.add(priorityDropdown, newTodoCons);

    cancelButton = new JButton("Cancel");
    newTodoCons.gridx = 0;
    newTodoCons.gridy = 5;
    newTodoCons.anchor = GridBagConstraints.WEST;
    cancelButton.addActionListener(this);
    cancelButton.setActionCommand("Cancel insert");
    newTodoPanel.add(cancelButton, newTodoCons);

    createButton = new JButton("Create");
    newTodoCons.gridx = 1;
    newTodoCons.gridy = 5;
    newTodoCons.anchor = GridBagConstraints.EAST;
    createButton.addActionListener(this);
    createButton.setActionCommand("Create");
    newTodoPanel.add(createButton, newTodoCons);

    newTodoPanel.setVisible(true);
    add(newTodoPanel);
    repaint();
    revalidate();

  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to create a new Todo

  private void createTodo() {

    heading.setForeground(Color.black);
    titleLabel.setForeground(Color.black);
    dueLabel.setForeground(Color.black);

    String titleInput = title.getText();
    boolean titleValid;
    String dueInput = dueBy.getText();
    boolean dueDateValid = false;
    LocalDateTime dueDateTime = null;
    String catInput = (String) catDropdown.getSelectedItem();
    Category category;
    String priorityInput = (String) priorityDropdown.getSelectedItem();
    Importance priority;
    Status status;
    String errorPrompt = "<html><p color='red'>";
    String titlePrompt = "Title must be 1-30 characters long, containing only letters, digits, spaces and apostrophes.<br>Title cannot be a single apostrophe<br>";
    String dueDatePassed = "Your Todo can't be due for completion in the past!<br>";
    String invalidDueDate = "The due date you entered is not valid<br>";

    titleValid = titleInput.matches("[A-Za-z0-9 ']{1,30}") && !(titleInput.equals("'"));
    if (!titleValid) {
      titleLabel.setForeground(Color.red);
      errorPrompt += titlePrompt;
    }

    try {
      dueDateTime = LocalDateTime.parse(dueInput);
      LocalDateTime now = LocalDateTime.now();
      dueDateValid = dueDateTime.isAfter(now);
      errorPrompt += dueDateValid ? "" : dueDatePassed;
    } catch (DateTimeParseException exc) {
      errorPrompt += invalidDueDate;
    }

    if (!dueDateValid) {
      dueLabel.setForeground(Color.red);
    }

    if (titleValid & dueDateValid) {

      category = Menu.stringToCat(catInput);
      priority = Menu.stringToImportance(priorityInput);
      status = Status.pending;

      Todo newTodo = new Todo(titleInput, dueDateTime, category, priority, status);
      todos.add(newTodo);
      todoIndex = todos.size() - 1;

      remove(newTodoPanel);
      todos = Menu.sortByDue(todos);
      defaultView();

      displayTodo(newTodo);

      repaint();
      revalidate();

    } else {
      errorPrompt += "</p></html>";
      heading.setText(errorPrompt);
    }

  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to create the 'update todo' form/view

  private void updatePanel() {

    Todo toBeUpdated = todos.get(todoIndex);

    updatePanel = new JPanel(new GridBagLayout());
    updatePanel.setPreferredSize(new Dimension(width / 3, height / 3));
    GridBagConstraints updateCons = new GridBagConstraints();

    String headingText = "Update Todo: " + toBeUpdated.getText();
    heading = new JLabel(headingText);
    updateCons.gridx = 0;
    updateCons.gridy = 0;
    updateCons.gridwidth = 2;
    updateCons.weighty = 0.75;
    updateCons.anchor = GridBagConstraints.CENTER;
    updatePanel.add(heading, updateCons);

    titleLabel = new JLabel("Title:");
    updateCons.gridx = 0;
    updateCons.gridy = 1;
    updateCons.anchor = GridBagConstraints.WEST;
    updatePanel.add(titleLabel, updateCons);

    title = new JTextField(toBeUpdated.getText(), 30);
    updateCons.gridx = 1;
    updateCons.gridy = 1;
    updateCons.weightx = 0.5;
    updateCons.anchor = GridBagConstraints.EAST;
    updatePanel.add(title, updateCons);

    dueLabel = new JLabel("<html>Due by <br>(YYYY-MM-DDTHH:MM):</html>");
    updateCons.gridx = 0;
    updateCons.gridy = 2;
    updateCons.anchor = GridBagConstraints.WEST;
    updatePanel.add(dueLabel, updateCons);

    LocalDateTime currDue = toBeUpdated.getDue();
    String currDueString = currDue.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    dueBy = new JTextField(currDueString, 16);
    updateCons.gridx = 1;
    updateCons.gridy = 2;
    updateCons.anchor = GridBagConstraints.EAST;
    updatePanel.add(dueBy, updateCons);

    catLabel = new JLabel("Category:");
    updateCons.gridx = 0;
    updateCons.gridy = 3;
    updateCons.anchor = GridBagConstraints.WEST;
    updatePanel.add(catLabel, updateCons);

    int catIndex = toBeUpdated.getCat().ordinal();
    catDropdown = new JComboBox(categories);
    updateCons.gridx = 1;
    updateCons.gridy = 3;
    updateCons.anchor = GridBagConstraints.EAST;
    catDropdown.setSelectedIndex(catIndex);
    updatePanel.add(catDropdown, updateCons);

    priorityLabel = new JLabel("Priority:");
    updateCons.gridx = 0;
    updateCons.gridy = 4;
    updateCons.anchor = GridBagConstraints.WEST;
    updatePanel.add(priorityLabel, updateCons);

    int priorIndex = toBeUpdated.getImportance().ordinal();
    priorityDropdown = new JComboBox(priorities);
    updateCons.gridx = 1;
    updateCons.gridy = 4;
    updateCons.anchor = GridBagConstraints.EAST;
    priorityDropdown.setSelectedIndex(priorIndex);
    updatePanel.add(priorityDropdown, updateCons);

    JLabel statusLabel = new JLabel("Status:");
    updateCons.gridx = 0;
    updateCons.gridy = 5;
    updateCons.anchor = GridBagConstraints.WEST;
    updatePanel.add(statusLabel, updateCons);

    int statusIndex = toBeUpdated.getCompletion().ordinal();
    String[] statuses = {"pending", "started", "partial", "completed"};
    statusDropdown = new JComboBox(statuses);
    updateCons.gridx = 1;
    updateCons.gridy = 5;
    updateCons.anchor = GridBagConstraints.EAST;
    statusDropdown.setSelectedIndex(statusIndex);
    updatePanel.add(statusDropdown, updateCons);

    cancelButton = new JButton("Cancel");
    updateCons.gridx = 0;
    updateCons.gridy = 6;
    updateCons.anchor = GridBagConstraints.WEST;
    cancelButton.addActionListener(this);
    cancelButton.setActionCommand("Cancel update");
    updatePanel.add(cancelButton, updateCons);

    createButton = new JButton("Update");
    updateCons.gridx = 1;
    updateCons.gridy = 6;
    updateCons.anchor = GridBagConstraints.EAST;
    createButton.addActionListener(this);
    createButton.setActionCommand("Update");
    updatePanel.add(createButton, updateCons);

    updatePanel.setVisible(true);
    add(updatePanel);
    repaint();
    revalidate();

  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to validate form data & update the provided Todo instance

  private void updateTodo() {

    heading.setForeground(Color.black);
    titleLabel.setForeground(Color.black);
    dueLabel.setForeground(Color.black);

    String newTitle = title.getText();
    String titlePrompt = "Title must be 1-30 characters long, containing only letters, digits, spaces and apostrophes.<br>Title cannot be a single apostrophe<br>";
    String dueBeforeRelease = "Your Todo can't be due for completion before this app was released!<br>";
    String errorPrompt = "<html><p>";

    boolean titleValid = newTitle.matches("[A-Za-z0-9 ']{1,30}") && !(newTitle.equals("'"));

    if (!titleValid) {
      titleLabel.setForeground(Color.red);
      errorPrompt += titlePrompt;
    }

    LocalDateTime dateInput = null;
    boolean validDate = false;
    String invalidDueDate = "The due date you entered is not valid<br>";

    try {
      dateInput = LocalDateTime.parse(dueBy.getText());
      validDate = dateInput.isAfter(LocalDateTime.parse("2021-05-06T09:00"));
      errorPrompt += validDate ? "" : dueBeforeRelease;
    } catch (DateTimeParseException exc) {
      errorPrompt += invalidDueDate;
      dueLabel.setForeground(Color.red);
    }

    if (titleValid && validDate) {

      Category newCat = Menu.stringToCat((String) catDropdown.getSelectedItem());

      Importance priority = Menu.stringToImportance((String) priorityDropdown.getSelectedItem());

      Status status = Menu.stringToStatus((String) statusDropdown.getSelectedItem());

      Todo toBeUpdated = todos.get(todoIndex);
      toBeUpdated.setText(newTitle);
      toBeUpdated.setDue(dateInput);
      toBeUpdated.setCat(newCat);
      toBeUpdated.setImportance(priority);
      toBeUpdated.setCompletion(status);

      remove(updatePanel);
      todos = Menu.sortByDue(todos);
      defaultView();

      displayTodo(toBeUpdated);

      repaint();
      revalidate();


    } else {
      errorPrompt += "</p></html>";
      heading.setForeground(Color.red);
      heading.setText(errorPrompt);
    }

  }


  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Method to delete an instance of Todo from the todos arrayList

  private void deleteTodo() {

    int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + todos.get(todoIndex).getText() + "?");

    if (option == 0) {
      todos.remove(todoIndex);
      todoIndex = -1;
      populateJList();
      remove(defaultView);
      todos = Menu.sortByDue(todos);
      defaultView();
      repaint();
      revalidate();
    }

  }

}