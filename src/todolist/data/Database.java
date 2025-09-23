package todolist.data;

import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 * This class constitutes the system's database.
 * It manages the list of tasks by offering
 * all the functionality of this management through methods, as well as
 * "reading" and "saving" the data files.
 */
public class Database
{
    final String dirPath = "csv_data";
    final String taskList = "tasks.csv";
    private final ArrayList<Task> tasks;
    SortBy sortBy;
    SortOrder sortOrder;
    Scanner keyboard;

    /**
     * The constructor of this class, it is used to initialize the tasks list
     */
    public Database()
    {
        tasks = new ArrayList<>();
        keyboard = new Scanner(System.in);

        createDataDirectory();
        createTaskFile();
        LoadTasks();
        sortTasks(SortBy.PRIORITY, SortOrder.ASCENDING);
    }

    /**
     * Creates the application data directory if it does not already exist.
     * The directory path is defined by {@code dirPath}.
     * This method ensures that the application has a proper storage location
     * for task files. If the directory is successfully created, the path is printed.
     * If it already exists, a message indicating that is displayed.
     */
    public void createDataDirectory()
    {
        File directory = new File (dirPath);
        if (!directory.exists())
        {
            if (directory.mkdirs())
                System.out.println("Directory created: " + directory.getAbsolutePath());
        }
        else
        {
            System.out.println("Directory already exists: " + directory.getAbsolutePath());
        }
    }

    /**
     * Creates the CSV file used to store tasks inside the application data directory.
     * If the file already exists, the method prints a message instead of overwriting it.
     * In case of an I/O error, the exception stack trace is displayed.
     */
    public void createTaskFile()
    {
        try
        {
            File obj = new File (dirPath, taskList);

            //Creating the File
            if (obj.createNewFile())
            {
                System.out.println("File created: " + obj.getAbsolutePath());
            }
            else
                System.out.println("File already exists: " + obj.getAbsolutePath());
        }
        catch (IOException ex)
        {
            System.out.println("An error has occurred while creating the File.");
            ex.printStackTrace();
        }
    }

    /**
     * Adds a new task to the list.
     * This method interacts with the user via the console to:
     *
     *  Enter a non-empty task description
     *  Select a task priority (HIGH, MEDIUM, LOW)
     *  Optionally provide a deadline (supports multiple date formats)
     *
     * After collecting input, a Task object is created, added to the
     * internal list, tasks are automatically sorted by priority, and changes are saved.
     */
    public void addTask()
    {
        // === Task Description ===
        System.out.println("---------------------");
        System.out.print("\nEnter task description: ");
        String description = keyboard.nextLine().trim();
        while(description.isEmpty())
        {
            System.out.println("\nDescription cannot be empty.");
            System.out.print("Enter again: ");
            description = keyboard.nextLine().trim();
        }

        // === Task Priority ===
        int choice;
        Priority priority = Priority.LOW; // default
        do
        {
            try
            {
                System.out.print("Enter task priority (1=HIGH, 2=MEDIUM, 3=LOW, empty = LOW): ");
                String input = keyboard.nextLine().trim();

                if (input.isEmpty())
                {
                    choice = 3;
                }
                else
                {
                    choice = Integer.parseInt(input);
                }

                switch (choice)
                {
                    case 1 -> priority = Priority.HIGH;
                    case 2 -> priority = Priority.MEDIUM;
                    case 3 -> priority = Priority.LOW;
                    default ->
                    {
                        System.out.println("\nInvalid input! Please enter 1, 2, or 3.");
                        choice = -1; // force repeat
                    }
                }
            }
            catch (NumberFormatException ex) // with nextInt we have InputMismatchException but with nextLine we have NumberFormatException
            {
                System.out.println("\nInvalid input! Please enter a number (1, 2, or 3), or leave empty for LOW.");
                choice = -1; // force repeat
            }
        }
        while(choice <= 0 || choice > 3);

        // === Task Deadline ===
        System.out.print("Enter deadline (yyyy-mm-ddTHH:mm) or leave empty for none: ");
        String deadlineInput = keyboard.nextLine().trim();

        LocalDateTime deadline = null;
        if (!deadlineInput.isEmpty())
        {
            try
            {
                // Try "yyyy-MM-dd HH:mm"
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                deadline = LocalDateTime.parse(deadlineInput, formatter);
            }
            catch (DateTimeParseException ex1)
            {
                try
                {
                    // Try ISO format (with T)
                    deadline = LocalDateTime.parse(deadlineInput);
                }
                catch (DateTimeParseException ex2)
                {
                    try
                    {    // Try "yyyy-MM-dd"
                        LocalDate date = LocalDate.parse(deadlineInput);
                        deadline = date.atStartOfDay();
                    }
                    catch (DateTimeParseException ex3)
                    {
                        System.out.println("Invalid deadline format. Task will have no deadline.");
                    }
                }
            }
        }

        // === Create Task ===
        Task tmp = new Task(description, priority, deadline);
        tasks.add(tmp);
        System.out.println("\n---------------------");
        System.out.println("Task added!");
        sortTasks(SortBy.PRIORITY, SortOrder.ASCENDING);  // Re-sort automatically
        saveTasks();
    }

    /**
     * Displays all tasks currently stored in memory.
     *
     * The method prints a formatted list of tasks along with their index.
     * It also indicates if tasks are currently sorted and by which criteria.
     * If no tasks exist, it informs the user. Execution pauses after displaying.
     */
    public void viewTasks()
    {
        int i = 1;
        System.out.println("\nYour tasks: ");
        System.out.println("=====================================================================");

        if (sortBy != null && sortOrder != null)
            System.out.println("Tasks sorted by " + sortBy + " (" + sortOrder + ")");

        if (tasks.isEmpty())
            System.out.println("Not tasks yet.");
        else
        {
            for (Task task : tasks)
            {
                System.out.println();
                System.out.println((i++) + ". " + task.toString());
            }
        }
        Pause();
    }

    /**
     * Removes a task from the list by user selection.
     *
     * Prompts the user to choose a task number, validates the input, and then
     * removes the corresponding task. After removal, the updated list is saved
     * back to the file system. If the input is invalid, the user is asked again.
     */
    public void removeTask()
    {
        if (tasks.isEmpty())
        {
            System.out.println("Not tasks to remove.");
            return;
        }

        int choice = -1;
        do
        {
            System.out.println("\nSelect the number of the task you want to remove: ");
            System.out.println("\n============================================================================");
            for (int i = 0; i < tasks.size(); i++)
            {
                System.out.println((i + 1) + ". " + tasks.get(i));
            }

            System.out.println("=============================================================================");
            System.out.print("\nEnter the task number you want to remove: ");
            String input = keyboard.nextLine().trim();

            try
            {
                choice = Integer.parseInt(input);
                if (choice > 0 && choice <= tasks.size())
                {
                    tasks.remove(choice -1);
                    System.out.println("Task removed!");
                    saveTasks();
                }
                else
                {
                    System.out.println("\nInvalid task number! Please try again.");
                    choice = - 1;
                }
            }
            catch (NumberFormatException ex)
            {
                System.out.println("\nInvalid input! Please enter a valid number.");
            }
        }
        while(choice == -1);

        Pause();
    }

    /**
     * Saves all tasks from memory to the task CSV file.
     *
     * Each task is serialized into a single CSV line using
     * {Task.AsCsvLine()}. Existing file contents are overwritten.
     * If the file cannot be found or written, an error message is displayed.
     */
    public void saveTasks()
    {
        File file = new File (dirPath, taskList);

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, false)))
        {
            for (Task task: tasks)
            {
                writer.println(task.AsCsvLine());
            }
        }
        catch (FileNotFoundException ex)
        {
            System.out.println("Cannot open tasks file. Not found.");
        }
        catch (IOException ex)
        {
            System.out.println("IO Error in saving tasks data. Should never happen.");
            ex.printStackTrace();
        }
    }

    /**
     * Loads tasks from the task CSV file into memory.
     * Reads each line of the file and attempts to parse it into a {@link Task}.
     * Parsing includes safe handling of:
     *
     *   Description
     *   Priority (defaults to MEDIUM if invalid)
     *   Deadline (supports multiple formats or left empty)
     *   Completion status
     *
     * Invalid lines are skipped with a warning. After loading, the total number
     * of tasks read is displayed.
     */
    public void LoadTasks()
    {
        File file = new File (dirPath, taskList);
        String line; // Variable for reading each line from the file
        String[] parts;
        String description;
        Priority priority;
        LocalDateTime deadline;
        boolean completed;

        Task newTask; // Task object to be created from each line

        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            while ((line = reader.readLine()) != null)
            {
                parts = line.split(","); // separation of line data based on ","
                if (parts.length < 2)
                {
                    System.out.println("Skipping invalid line (too few fields): " + line);
                    continue;
                }

                description = parts[0].trim();

                // parse priority safely
                try
                {
                    priority = Priority.valueOf(parts[1].trim().replaceAll("[\\[\\]]", ""));
                }
                catch (IllegalArgumentException ex)
                {
                    System.out.println("Invalid priority in line, defaulting to MEDIUM: " + line);
                    priority = Priority.MEDIUM;
                }

                // parse deadline safely (allow empty or date-only or full datetime)
                deadline = null;
                if (parts.length > 2 && !parts[2].trim().isEmpty())
                {
                    String deadlineString = parts[2].trim();
                    try
                    {
                        // Try with space: yyyy-MM-dd HH:mm
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        deadline = LocalDateTime.parse(deadlineString, formatter);
                    }
                    catch (DateTimeParseException ex1)
                    {
                        try
                        {
                            // Try ISO format (with T): yyyy-MM-ddTHH:mm
                            deadline = LocalDateTime.parse(deadlineString);
                        }
                        catch (DateTimeParseException ex2)
                        {
                            try
                            {
                                // Try only date
                                LocalDate date = LocalDate.parse(deadlineString);
                                deadline = date.atStartOfDay();
                            }
                            catch(DateTimeParseException ex3)
                            {
                                if (!deadlineString.isEmpty())
                                    System.out.println("Invalid deadline format, leaving null: " + line);
                            }
                        }
                    }
                }

                completed = false;
                if (parts.length >= 4)
                {
                    completed = Boolean.parseBoolean(parts[3].trim());
                }

                newTask = new Task(description, priority, deadline); // New task creation
                newTask.setCompleted(completed);
                tasks.add(newTask);
            }
        }
        catch (FileNotFoundException ex)
        {
            System.out.println("Cannot Open File: " + file.getAbsolutePath());
            return;
        }
        catch (IOException ex)
        {
            System.out.println("Error reading file: " + file.getAbsolutePath());
            ex.printStackTrace();
            //return;
        }

        System.out.println("Data read successfully. Total tasks: " + tasks.size());
    }

    /**
     * Sorts tasks according to the specified criteria and order.
     *
     * The order can be ascending or descending. After sorting, the preferences
     * are stored and the tasks are displayed.
     *
     * @param sortBy The attribute to sort by (priority or deadline).
     * @param sortOrder The sorting order (ascending or descending).
     */
    public void sortTasks(SortBy sortBy, SortOrder sortOrder)
    {
        Comparator<Task> comparator;

        switch (sortBy)
        {
            case PRIORITY:
                    comparator = Comparator.comparing(Task::getPriority); break;
            case DEADLINE:
                comparator = Comparator.comparing(Task::getDeadline, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            default:
                throw new IllegalArgumentException("Unknown sort option");
        }

        // Reverse if user chose descending
        if (sortOrder == SortOrder.DESCENDING)
        {
            comparator = comparator.reversed();
        }
        tasks.sort(comparator);

        // Save sorting preferences for display
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;

        viewTasks();
    }

    /**
     * Provides an interactive menu for sorting tasks.
     *
     * Input is validated, and the corresponding sorting method is called.
     */
    public void sortTasksMenu()
    {
        int choice = -1;
        do
        {
            System.out.println("\nChoose Sorting Option:");
            System.out.println("----------------------");
            System.out.println("1. Priority ascending");
            System.out.println("2. Priority descending");
            System.out.println("3. Deadline ascending");
            System.out.println("4. Deadline descending");
            System.out.println("----------------------");

            System.out.print("Enter choice: ");
            String input = keyboard.nextLine().trim();

            try
            {
                choice = Integer.parseInt(input);
                switch (choice)
                {
                    case 1 -> sortTasks(SortBy.PRIORITY, SortOrder.ASCENDING);
                    case 2 -> sortTasks(SortBy.PRIORITY, SortOrder.DESCENDING);
                    case 3 -> sortTasks(SortBy.DEADLINE, SortOrder.ASCENDING);
                    case 4 -> sortTasks(SortBy.DEADLINE, SortOrder.DESCENDING);
                    default ->
                    {
                        System.out.println("Invalid choice! Enter a number 1-4.");
                        choice = -1;
                    }
                }
            }
            catch (NumberFormatException ex)
            {
                System.out.println("Invalid input! Please enter a number between 1 and 4.");
            }
        }
        while(choice == -1);
    }

    /**
     * Toggles the completion status of a selected task.
     *
     * The user is prompted to select a task by number. If valid, the task’s
     * completion flag is inverted (completed ↔ incomplete). Changes are saved
     * to the task file afterward.
     */
    public void toggleTaskCompletion()
    {
        if (tasks.isEmpty())
        {
            System.out.println("No tasks available.");
            return;
        }

        int choice = -1;
        do
        {
            System.out.println("\nSelect the number of the task to mark complete/incomplete: \n");
            System.out.println("=====================================================================================");
            for (int i = 0; i < tasks.size(); i++)
            {
                System.out.println((i + 1) + "." + tasks.get(i));
            }
            System.out.println("=====================================================================================");

            System.out.print("\nEnter task number: ");
            String input = keyboard.nextLine().trim();

            try
            {
                choice = Integer.parseInt(input);
                if (choice > 0 && choice <= tasks.size())
                {
                    Task task = tasks.get(choice - 1);
                    task.setCompleted(!task.isCompleted());
                    System.out.println("\nTask \"" + task.getDescription() + "\" marked as " + (task.isCompleted() ? "completed" : "incomplete") + "!");
                    saveTasks();
                }
                else
                {
                    System.out.println("\nInvalid task number! Try again.");
                    choice = -1;
                }
            }
            catch(NumberFormatException ex)
            {
                System.out.println("\nInvalid Input! Please enter a valid number.");
            }
        }
        while(choice == -1);

        Pause();
    }

    /**
     * Pauses program execution until the user presses Enter.
     *
     * This method is intended for console-based interaction only and is skipped
     * in GUI mode. It ensures the user has time to read console output before
     * proceeding.
     */
    public void Pause()
    {
        boolean interactiveMode = true; // set false in GUI mode
        if (!interactiveMode) return; // skip pause in GUI mode

        System.out.print("\n\nPress <Enter> to continue....");

        // flush any leftover input
        while (keyboard.hasNextLine())
        {
            String leftover = keyboard.nextLine();
            if (leftover.isEmpty()) break;
        }

        System.out.println();
    }
}
