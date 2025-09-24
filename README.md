# Basic CLI To-Do List

## Description
A simple command-line To-Do List application written in Java.  
It demonstrates Java basics such as loops, collections, input handling, file storage, and menu-driven programming.

## Features
- Add, view, remove, and complete tasks.
- Task priorities (HIGH, MEDIUM, LOW).
- Optional task deadlines.
- Automatic sorting by priority or deadline.
- Saves tasks to a CSV file for persistence.

## How to Run
To run this program locally:

1. **Install Java JDK 21** (or a compatible version).
2. **Clone this repository**:
   ```bash
   git clone https://github.com/Lerghis/basic-todo-list-cli.git
   
3. **Open the project in IntelliJ IDEA** (or any Java IDE).
4. **Run the ToDoListApp class** (contains the main method).
5. **Follow the menu in the terminal to manage your tasks.**

## Data Storage
When you run the program for the first time, it will automatically create a folder called **csv_data** and a file called **tasks.csv** in it. This file will store all your tasks. If **tasks.csv** does not exist, the program will create it automatically. 

The format of the file is:

```text
description,priority,deadline,completed
```
Example entry: 

```text
Finish homework,High,2025-09-24,false
```

Example Usage: 

```text
==== To-Do List Menu ====
1. Add a task
2. View tasks
3. Mark task as completed
4. Delete task
5. Exit
Enter your choice:

