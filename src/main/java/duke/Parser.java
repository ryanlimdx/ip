package duke;

import duke.commands.Find;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.Todo;
import duke.utils.Ui;

/**
 * The Parser class handles parsing of user input for Duke chatbot.
 * It interprets user commands and executes corresponding actions.
 */
public class Parser {
    /**
     * Parses the user input and executes the corresponding actions.
     *
     * @param userTasks The TaskList containing the user's tasks.
     * @param currInput The current user input.
     */
    public static String parse(TaskList userTasks, String[] currInput) {
        try {
            String cmd = currInput[0];
            boolean isAdded = false;

            // list tasks
            if (cmd.equals("list")) {
                System.out.println(userTasks.stringifyTasks(false));
            // find tasks
            } else if (cmd.equals("find")) {
                return new Find(currInput, userTasks).execute();
            // mark tasks
            } else if (cmd.contains("mark")) {
                if (currInput.length < 2) {
                    throw new DukeException(
                            String.format(DukeException.NON_EMPTY_DESC, cmd));
                }
                int task = Integer.parseInt(currInput[1]);
                int numTasks = userTasks.getSize();
                if (task > numTasks) {
                    throw new DukeException("you have " + numTasks
                            + " tasks. Please fill in a number lower than or equal to it!"
                    );
                }

                int taskIdx = task - 1;
                Task currTask = userTasks.getTask(taskIdx);

                if (cmd.equals("mark")) {
                    currTask.markAsDone();
                    System.out.println("    Nice! I have marked this task as done: \n"
                            + "        " + currTask);
                } else if (cmd.equals("unmark")) {
                    currTask.markAsUndone();
                    System.out.println("    Ok, I've marked this task as not done yet: \n"
                            + "        " + currTask);
                }

            // delete tasks
            } else if (cmd.equals("delete")) {
                if (currInput.length < 2) {
                    throw new DukeException(
                            String.format(DukeException.NON_EMPTY_DESC, cmd));
                }
                int task = Integer.parseInt(currInput[1]);
                int numTasks = userTasks.getSize();
                if (task > numTasks) {
                    throw new DukeException("you have " + numTasks
                            + " tasks. Please fill in a number lower than or equal to it!"
                    );
                }
                int taskIdx = task - 1;

                Task delTask = userTasks.rmvTask(taskIdx);
                System.out.println("    Noted. I've removed this task:\n"
                        + "        " + delTask + "\n"
                        + "    Now you have " + (numTasks - 1) + " tasks in the list."
                );

            // add tasks
            } else if (cmd.equals("deadline")) {
                if (currInput.length < 2) {
                    throw new DukeException(
                            String.format(DukeException.NON_EMPTY_DESC, cmd));
                }
                String[] task = currInput[1].split(" /by ");
                Deadline newDL = new Deadline(task[0], task[1]);
                userTasks.addTask(newDL);
                isAdded = true;
            } else if (cmd.equals("event")) {
                if (currInput.length < 2) {
                    throw new DukeException(
                            String.format(DukeException.NON_EMPTY_DESC, cmd));
                }
                String[] task = currInput[1].split(" /from ", 2);
                String[] period = task[1].split(" /to ", 2);
                Event newEvt = new Event(task[0], period[0], period[1]);
                userTasks.addTask(newEvt);
                isAdded = true;
            } else if (cmd.equals("todo")) {
                if (currInput.length < 2) {
                    throw new DukeException(
                            String.format(DukeException.NON_EMPTY_DESC, cmd));
                }
                Todo newTd = new Todo(currInput[1]);
                userTasks.addTask(newTd);
                isAdded = true;

            // unknown commands
            } else {
                throw new DukeException(String.format(DukeException.UNKNOWN_CMD, cmd));
            }

            // Check if task is added to add appropriate syntax.
            if (isAdded) {
                int numTasks = userTasks.getSize();
                Task addedTask = userTasks.getTask(numTasks - 1);
                System.out.println("    Got it. I've added this task:\n"
                        + "        " + addedTask + "\n"
                        + "    Now you have " + numTasks + " tasks in the list."
                );
            }
        } catch (DukeException e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println(Ui.LINE);
        }
    }
}
