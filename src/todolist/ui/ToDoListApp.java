package todolist.ui;

import todolist.data.Database;

import java.io.IOException;
import java.util.Scanner;

public class ToDoListApp
{
    Database DataBase;
    Scanner keyboard;

    public ToDoListApp()
    {
        DataBase = new Database();
        keyboard = new Scanner(System.in);
    }

    public void Menu()
    {
        int choice;
        do
        {
            //Cls();
            System.out.println("\n\n    M E N U");
            System.out.println("===================\n");
            System.out.println("[1].......Add Task");
            System.out.println("[2].......View Tasks");
            System.out.println("[3].......Remove Task");
            System.out.println("[4].......Sort Tasks");
            System.out.println("[5].......Mark Tasks as Complete/Incomplete");
            System.out.println("[6].......Exit App");
            System.out.print("\nEnter your choice: ");
            choice = keyboard.nextInt();
            keyboard.nextLine(); // Clear Buffer

            switch (choice)
            {
                case 1 -> DataBase.addTask();
                case 2 -> DataBase.viewTasks();
                case 3 -> DataBase.removeTask();
                case 4 -> DataBase.sortTasksMenu();
                case 5 -> DataBase.toggleTaskCompletion();
                case 6 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice!");
            }
        }
        while(choice != 6);
    }

    // Calling Save methods to save the data
    public void FinalJobs()
    {
        DataBase.saveTasks();
    }
    public static void main(String[] args)
    {
        ToDoListApp Program = new ToDoListApp();
        Program.Menu();
        Program.FinalJobs();
        Program.keyboard.close();
    }

    public void Cls()
    {

        try
        {
            new ProcessBuilder ("cmd", "/c", "cls").inheritIO().
                    start(). waitFor();
        }
        catch (IOException ex)
        {

        }
        catch (InterruptedException ex)
        {

        }
    }
    public void Pause()
    {
        System.out.print ("\n\nPress <Enter> to continue....");
        keyboard.nextLine();
        System.out.println ("\n");
    }
}