package com.rndapp.task_feed.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.rndapp.task_feed.models.Task;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 2:58 PM
 *
 */
public class TaskDataSource {
    // Database fields
    private SQLiteDatabase database;
    private TaskOpenHelper dbHelper;
    private String[] allColumns = { TaskOpenHelper.COLUMN_ID,
            TaskOpenHelper.COLUMN_SERVER_ID,
            TaskOpenHelper.COLUMN_PROJECT_SERVER_ID,
            TaskOpenHelper.COLUMN_TEXT,
            TaskOpenHelper.COLUMN_COMPLETED,
            TaskOpenHelper.COLUMN_POSITION,
            TaskOpenHelper.COLUMN_CREATED,
            TaskOpenHelper.COLUMN_UPDATED,
            TaskOpenHelper.COLUMN_POINTS};

    public TaskDataSource(Context context) {
        dbHelper = new TaskOpenHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Task createTask(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskOpenHelper.COLUMN_SERVER_ID, task.getId());
        values.put(TaskOpenHelper.COLUMN_PROJECT_SERVER_ID, task.getProject_id());
        values.put(TaskOpenHelper.COLUMN_POINTS, task.getPoints());
        values.put(TaskOpenHelper.COLUMN_UPDATED, task.getUpdated_at().getTime());
        values.put(TaskOpenHelper.COLUMN_POSITION, task.getOrder());
        int complete = task.isFinished() ? 1 : 0;
        values.put(TaskOpenHelper.COLUMN_COMPLETED, complete);
        values.put(TaskOpenHelper.COLUMN_TEXT, task.getName());

        long insertId = database.insert(TaskOpenHelper.TABLE_TASKS, null,
                values);
        Cursor cursor = database.query(TaskOpenHelper.TABLE_TASKS,
                allColumns, TaskOpenHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Task newTask = cursorToTask(cursor);
        cursor.close();
        return newTask;
    }

    public void updateTask(Task task){
        ContentValues values = new ContentValues();
        values.put(TaskOpenHelper.COLUMN_SERVER_ID, task.getId());
        values.put(TaskOpenHelper.COLUMN_PROJECT_SERVER_ID, task.getProject_id());
        values.put(TaskOpenHelper.COLUMN_POINTS, task.getPoints());
        values.put(TaskOpenHelper.COLUMN_UPDATED, task.getUpdated_at().getTime());
        values.put(TaskOpenHelper.COLUMN_POSITION, task.getOrder());
        int complete = task.isFinished() ? 1 : 0;
        values.put(TaskOpenHelper.COLUMN_COMPLETED, complete);
        values.put(TaskOpenHelper.COLUMN_TEXT, task.getName());

        database.update(TaskOpenHelper.TABLE_TASKS, values, TaskOpenHelper.COLUMN_SERVER_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
    }

    public void deleteTask(Task task) {
        long id = task.getLocalId();
        System.out.println("Task deleted with id: " + id);
        database.delete(TaskOpenHelper.TABLE_TASKS, TaskOpenHelper.COLUMN_ID
                + " = " + id, null);
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<Task>();

        Cursor cursor = database.query(TaskOpenHelper.TABLE_TASKS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return tasks;
    }

    private Task cursorToTask(Cursor cursor) {
        Task task = new Task();
        task.setId(cursor.getInt(cursor.getColumnIndex(TaskOpenHelper.COLUMN_SERVER_ID)));
        task.setLocalId(cursor.getInt(cursor.getColumnIndex(TaskOpenHelper.COLUMN_ID)));
        task.setProject_id(cursor.getInt(cursor.getColumnIndex(TaskOpenHelper.COLUMN_PROJECT_SERVER_ID)));
        task.setName(cursor.getString(cursor.getColumnIndex(TaskOpenHelper.COLUMN_TEXT)));
        task.setPoints(cursor.getInt(cursor.getColumnIndex(TaskOpenHelper.COLUMN_POINTS)));
        task.setOrder(cursor.getInt(cursor.getColumnIndex(TaskOpenHelper.COLUMN_POSITION)));
        task.setFinished(1 == cursor.getInt(cursor.getColumnIndex(TaskOpenHelper.COLUMN_COMPLETED)));
        task.setUpdated_at(new Date(cursor.getLong(cursor.getColumnIndex(TaskOpenHelper.COLUMN_UPDATED))));
        return task;
    }
}
