package todolist.data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Task
{
    private String description;
    private Priority priority;
    private LocalDateTime deadline;
    private boolean completed;

    public Task(String description, Priority priority, LocalDateTime deadline)
    {
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.completed = false; // default when adding a task
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    /**
     * Converts the information into a single CSV file
     * @return the CSV line that includes the Task data in String format.
     */
    public String AsCsvLine()
    {
        String deadLineString = (deadline == null ? "" : deadline.toString());
        return description + "," + priority + "," + deadLineString + "," + completed;
    }
    @Override
    public String toString()
    {
        String priorityString = "Priority: " + priority;
        String status = "Status: " + (completed ? "[✔]" : "[ ]"); // or Completed" : "Incomplete
        String deadlineString;

        if (deadline == null)
        {
            deadlineString = "Deadline: No deadline";
        }
        else if (deadline.toLocalTime().equals(LocalTime.MIDNIGHT))
        {
            // show only date
            deadlineString = "Deadline: " + deadline.toLocalDate();
        }
        else
        {
            // show date and time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
            deadlineString = "Deadline: " + deadline.format(formatter);
        }

        return description + " | " + priorityString + " | " + deadlineString + " | " + status;
    }
}
