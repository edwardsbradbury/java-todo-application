package com.company;

import java.time.LocalDateTime;

/* Class to create instances of Todo, with methods to get & set the instance properties */
public class Todo {

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Class instance variables
  private String text;
  private LocalDateTime due;
  private Category cat;
  private Importance importance;
  private Status completion;

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Getters and setters

  public String getText() {
    return text;
  }

  public void setText(String newText) {
    this.text = newText;
  }

  public LocalDateTime getDue() {
    return due;
  }

  public void setDue(LocalDateTime newDue) {
    this.due = newDue;
  }

  public Category getCat() {
    return cat;
  }

  public void setCat(Category newCat) {
    this.cat = newCat;
  }

  public Importance getImportance() {
    return importance;
  }

  public void setImportance(Importance newImportance) {
    this.importance = newImportance;
  }

  public Status getCompletion() {
    return completion;
  }

  public void setCompletion(Status newCompletion) {
    this.completion = newCompletion;
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // Constructor

  Todo(String initText, LocalDateTime initDue, Category initCat, Importance initImportance, Status initCompletion) {
    this.text = initText;
    this.due = initDue;
    this.cat = initCat;
    this.importance = initImportance;
    this.completion = initCompletion;
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  // toString method

  public String toString() {
    return cat.getColour() + "Todo{\nTitle: " + this.text + "\nDue by: " + this.due + "\nImportance: " + this.importance + "\nStatus: " + this.completion + "\n}\033[0m";
  }

}
