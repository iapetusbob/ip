package DukeHelpfulCode.Commands;

import DukeHelpfulCode.Exceptions.*;

import DukeHelpfulCode.Tasks.Deadline;
import DukeHelpfulCode.Tasks.Event;
import DukeHelpfulCode.Tasks.Task;
import DukeHelpfulCode.Tasks.Todo;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDateTime;

import java.util.Arrays;
import java.util.Locale;

public class AddCommandParser {

    private static String[] possibleFormats = {
            "yyyy-MM-dd HHmm", "yyyy/MM/dd HHmm",
            "dd-MM-yyyy HHmm", "dd/MM/yyyy HHmm",
            "yyyy-MM-dd hh:mm a", "yyyy/MM/dd hh:mm a",
            "dd-MM-yyyy hh:mm a", "dd/MM/yyyy hh:mm a",
            "HHmm yyyy-MM-dd", "HHmm yyyy/MM/dd",
            "HHmm dd-MM-yyyy", "HHmm dd/MM/yyyy",
            "hh:mm a yyyy-MM-dd", "hh:mm a yyyy/MM/dd",
            "hh:mm a dd-MM-yyyy", "hh:mm a dd/MM/yyyy",
            "yy-MM-dd HHmm", "yy/MM/dd HHmm",
            "dd-MM-yy HHmm", "dd/MM/yy HHmm",
            "yy-MM-dd hh:mm a", "yy/MM/dd hh:mm a",
            "dd-MM-yy hh:mm a", "dd/MM/yy hh:mm a",
            "HHmm yy-MM-dd", "HHmm yy/MM/dd",
            "HHmm dd-MM-yy", "HHmm dd/MM/yy",
            "hh:mm a yy-MM-dd", "hh:mm a yy/MM/dd",
            "hh:mm a dd-MM-yy", "hh:mm a dd/MM/yy",
            "dd MMM yyyy hh:mm a"};

    public static Command parse(String[] userInput) throws NoTaskTypeException, NoTaskNameException {
        Task task;
        String taskType;
        String taskName = "";

        try {
            taskType = userInput[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NoTaskTypeException();
        }

//        try {
//            String test = userInput[1];
//        } catch (ArrayIndexOutOfBoundsException e) {
//            throw new NoTaskNameException();
//        }

        if (taskType.toLowerCase(Locale.ROOT).equals("todo")) {
            for (int i = 2; i < userInput.length; i++) {
                taskName += userInput[i] + " ";
            }
            task = new Todo(taskName);

        } else if (taskType.toLowerCase(Locale.ROOT).equals("event")) {

            String start = "";
            String end = "";
            int i = 2;

            if (!Arrays.asList(userInput).contains("/from") || !Arrays.asList(userInput).contains("/to")) {
                return new ErrorCommand("Sorry, I don't understand when this Event starts or ends.");
            }

            while (!userInput[i].equals("/from")) {
                taskName += userInput[i] + " ";
                i++;
            }
            if (taskName.equals("")) {
                throw new NoTaskNameException();
            }
            i++;
            while (!userInput[i].equals("/to")) {
                start += userInput[i] + " ";
                i++;
            }
            i++;
            for (int j = i; j < userInput.length; j++) {
                end += userInput[j] + " ";
            }
            //System.out.println(Arrays.asList(userInput));
            task = new Event(taskName, formatDateTime(start.substring(0,start.length()-1)), formatDateTime(end.substring(0, end.length()-1)));

        } else if (taskType.toLowerCase(Locale.ROOT).equals("deadline")) {
            String by = "";
            int i = 2;
            if (!Arrays.asList(userInput).contains("/by")) {
                return new ErrorCommand("Sorry, I don't understand when this Deadline is due.");
            }
            while (!userInput[i].equals("/by")) {
                taskName += userInput[i] + " ";
                i++;
            }
            i++;
            for (int j = i; j < userInput.length; j++) {
                 by += userInput[j] + " ";
            }
            task = new Deadline(taskName,formatDateTime(by.substring(0,by.length()-1)));
        } else {
            throw new NoTaskTypeException();
        }

        return new AddCommand(task);
    }
    private static LocalDateTime formatDateTime(String dueDate) {
        LocalDateTime dt = null;
        for (String format : possibleFormats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                dt = LocalDateTime.parse(dueDate, formatter);
                break;
            } catch (DateTimeParseException e) {
                // Do nothing, just continue to the next format
            }
        }
        return dt;
    }
}
