package com.example.todowithsqlite;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todowithsqlite.adapter.TaskAdapter;
import com.example.todowithsqlite.database.DatabaseHelper;
import com.example.todowithsqlite.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskListener {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private DatabaseHelper databaseHelper;
    private List<Task> taskList;
    private FloatingActionButton fabAddTask;
    
    private Calendar selectedDate;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Initialize date format
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        
        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);
        
        // Initialize UI components
        recyclerView = findViewById(R.id.recycler_tasks);
        fabAddTask = findViewById(R.id.fab_add_task);
        
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Load tasks from database
        loadTasks();
        
        // Set up FAB click listener
        fabAddTask.setOnClickListener(v -> showAddTaskDialog(null));
    }
    
    private void loadTasks() {
        taskList = databaseHelper.getAllTasks();
        taskAdapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(taskAdapter);
    }
    
    private void showAddTaskDialog(Task taskToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);
        
        // Initialize dialog components
        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextInputEditText editTitle = dialogView.findViewById(R.id.edit_task_title);
        TextInputEditText editDescription = dialogView.findViewById(R.id.edit_task_description);
        Button buttonDatePicker = dialogView.findViewById(R.id.button_date_picker);
        TextView textSelectedDate = dialogView.findViewById(R.id.text_selected_date);
        
        // Set up date picker
        selectedDate = Calendar.getInstance();
        
        // If editing a task, populate fields
        boolean isEditing = taskToEdit != null;
        if (isEditing) {
            dialogTitle.setText("Edit Task");
            editTitle.setText(taskToEdit.getTitle());
            editDescription.setText(taskToEdit.getDescription());
            
            try {
                // Try to parse the existing date
                textSelectedDate.setText(taskToEdit.getDate());
            } catch (Exception e) {
                textSelectedDate.setText("No date selected");
            }
        }
        
        // Set up date picker button
        buttonDatePicker.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        textSelectedDate.setText(dateFormat.format(selectedDate.getTime()));
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
        
        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save", (dialogInterface, i) -> {
            String title = editTitle.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String date = textSelectedDate.getText().toString();
            
            if (title.isEmpty()) {
                Toast.makeText(MainActivity.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (date.equals("No date selected")) {
                date = dateFormat.format(selectedDate.getTime()); // Use current date
            }
            
            if (isEditing) {
                // Update existing task
                taskToEdit.setTitle(title);
                taskToEdit.setDescription(description);
                taskToEdit.setDate(date);
                databaseHelper.updateTask(taskToEdit);
            } else {
                // Create new task
                Task newTask = new Task(title, description, date, false);
                long id = databaseHelper.insertTask(newTask);
                newTask.setId(id);
            }
            
            // Refresh task list
            loadTasks();
        });
        
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (dialogInterface, i) -> {
            dialog.dismiss();
        });
        
        dialog.show();
    }

    @Override
    public void onTaskClick(int position) {
        // Edit task when clicked
        Task task = taskAdapter.getTaskAt(position);
        showAddTaskDialog(task);
    }

    @Override
    public void onTaskCheckChanged(int position, boolean isChecked) {
        // Update task completion status
        Task task = taskAdapter.getTaskAt(position);
        task.setCompleted(isChecked);
        databaseHelper.updateTask(task);
        taskAdapter.notifyItemChanged(position);
    }

    @Override
    public void onTaskDelete(int position) {
        // Delete task
        Task task = taskAdapter.getTaskAt(position);
        databaseHelper.deleteTask(task.getId());
        loadTasks(); // Refresh the list
        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
    }
}