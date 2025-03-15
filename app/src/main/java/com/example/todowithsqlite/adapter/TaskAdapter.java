package com.example.todowithsqlite.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todowithsqlite.R;
import com.example.todowithsqlite.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskListener onTaskListener;

    public interface OnTaskListener {
        void onTaskClick(int position);
        void onTaskCheckChanged(int position, boolean isChecked);
        void onTaskDelete(int position);
    }

    public TaskAdapter(List<Task> taskList, OnTaskListener onTaskListener) {
        this.taskList = taskList;
        this.onTaskListener = onTaskListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view, onTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.titleTextView.setText(task.getTitle());
        holder.descriptionTextView.setText(task.getDescription());
        holder.dateTextView.setText(task.getDate());
        holder.checkBox.setChecked(task.isCompleted());
        
        // Apply strike-through text if task is completed
        if (task.isCompleted()) {
            // Apply visual indication that task is completed
            holder.titleTextView.setAlpha(0.5f);
            holder.descriptionTextView.setAlpha(0.5f);
        } else {
            holder.titleTextView.setAlpha(1.0f);
            holder.descriptionTextView.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTasks(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    public Task getTaskAt(int position) {
        return taskList.get(position);
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, dateTextView;
        CheckBox checkBox;
        ImageButton deleteButton;
        OnTaskListener onTaskListener;

        public TaskViewHolder(@NonNull View itemView, OnTaskListener onTaskListener) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_task_title);
            descriptionTextView = itemView.findViewById(R.id.text_task_description);
            dateTextView = itemView.findViewById(R.id.text_task_date);
            checkBox = itemView.findViewById(R.id.checkbox_task);
            deleteButton = itemView.findViewById(R.id.button_delete);
            this.onTaskListener = onTaskListener;

            itemView.setOnClickListener(v -> {
                if (onTaskListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onTaskListener.onTaskClick(position);
                    }
                }
            });

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (onTaskListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onTaskListener.onTaskCheckChanged(position, isChecked);
                    }
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (onTaskListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onTaskListener.onTaskDelete(position);
                    }
                }
            });
        }
    }
}