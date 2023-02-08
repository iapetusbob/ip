package DukeHelpfulCode.Utilities;

import java.io.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.List;

import DukeHelpfulCode.Tasks.*;
import DukeHelpfulCode.Exceptions.*;

public class Storage {

    public String filePath;

    public Storage(String filePath){
        this.filePath = filePath;
    }

    // should have a read, write and search function in this class

    private Task taskFromText(String[] s){
        String type = s[0]; // [T] = todo | [D] = deadline | [E] = event
        boolean isDone = s[1].equals("[X]"); // note that if not done, s[1] will be "["
        String name = "";
        Task t = null;
        int i = isDone ? 2 : 3;
        if (type.equals("[T]")){
            for (; i < s.length; i++){
                name += s[i] + " ";
            }
            t = new Todo(name, isDone);
        } else if (type.equals("[D]")){
            while (!s[i].equals("(by:")){
                name += s[i] + " ";
                i++;
            }
            i++; // to skip the "(by:"
            String dd = s[i] + " " + s[i+1] + " " + s[i+2] + " " + s[i+3] + " " + s[i+4];
            // dd MMM yy hh:mm a)
            t = new Deadline(name, dd.substring(0,dd.length()-1), isDone);
        } else if (type.equals("[E]")){
            while (!s[i].equals("(from:")){
                name += s[i] + " ";
                i++;
            }
            i++; // to skip the "(from:"
            String sd = s[i] + " " + s[i+1] + " " + s[i+2] + " " + s[i+3] + " " + s[i+4];
            // dd MMM yy hh:mm a)
            i+=6; // to skip "to:"
            String ed = s[i] + " " + s[i+1] + " " + s[i+2] + " " + s[i+3] + " " + s[i+4];
            // dd MMM yy hh:mm a)
            t = new Event(name, sd, ed.substring(0,ed.length()-1),isDone);
        }

        return t;

    }

    private LocalDateTime formatDateTime(String s) {
        LocalDateTime dt = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a");
             dt = LocalDateTime.parse(s, formatter);
        } catch (DateTimeParseException e) {
            // Do nothing, just continue to the next format
        }
        return dt;
    }

    public List<Task> load() throws EmptyTaskListException, IOException {
        /**
         * Loads the currently existing tasks and stuff? not sure
         * this function goes into the new Tasklist()
         * if empty return empty task list
         * if not empty return the task list
         */
        File taskListText = null;
        List<Task> taskList = new ArrayList<>();
        try {
            taskListText = new File(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (taskListText.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(this.filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                taskList.add(taskFromText(line.split(" ")));
            }
            if (taskList.size() == 0) {
                throw new EmptyTaskListException();
            }
        } else {
            taskListText.createNewFile();
        }

        return taskList;

    }

    public void write(TaskList tl) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.filePath));
        for (int i = 0; i < tl.len(); i++){
            System.out.println(tl.taskList.get(i).toString());
            writer.write(tl.taskList.get(i).toString()+"\n");
            writer.flush();
        }
    }

}
