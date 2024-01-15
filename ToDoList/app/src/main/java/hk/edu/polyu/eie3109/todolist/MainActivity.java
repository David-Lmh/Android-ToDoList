package hk.edu.polyu.eie3109.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView RVTaskList;
    ArrayList<TaskModel> taskModelArrayList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    FloatingActionButton floatingActionButton;
    RVAdapter RVAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RVTaskList = findViewById(R.id.RVTaskList);
        RVTaskList.setLayoutManager(new LinearLayoutManager(this));

        sharedPreferences = getSharedPreferences("TaskModel", Context.MODE_PRIVATE);
        setUpTaskModels();

        RVAdapter = new RVAdapter(this, taskModelArrayList);
        RVTaskList.setAdapter(RVAdapter);

        floatingActionButton = findViewById(R.id.FABAddTask);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();

                Dialog addForm = new Dialog(MainActivity.this);
                addForm.requestWindowFeature(Window.FEATURE_NO_TITLE);
                addForm.setContentView(R.layout.form_layout);

                Button BNSave = addForm.findViewById(R.id.BNSubmit);
                BNSave.setText("Add");
                BNSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText ETNewTask = addForm.findViewById(R.id.ETTaskText);
                        taskModelArrayList.add(new TaskModel(ETNewTask.getText().toString(), false));
                        RVAdapter.notifyDataSetChanged();
                        saveTaskModels();
                        addForm.dismiss();
                    }
                });
                addForm.show();
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(RVAdapter));
        itemTouchHelper.attachToRecyclerView(RVTaskList);
    }

    private void setUpTaskModels() {
        if (sharedPreferences.getInt("TaskModel Size", 0) == 0) {
            String[] taskStrings = new String[20];

            for (int i = 0; i < 20; i++) {
                taskStrings[i] = "Task " + i;
            }

            for (int i = 0; i < taskStrings.length; i++) {
                taskModelArrayList.add(new TaskModel(taskStrings[i], i % 2 == 0));
            }
        } else {
            loadTaskModels();
        }
    }

    private void saveTaskModels() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("TaskModel Size", taskModelArrayList.size());
        for (int i = 0; i < taskModelArrayList.size(); i++) {
            editor.putString("TaskModel " + i + " TaskString", taskModelArrayList.get(i).getTaskString());
            editor.putBoolean("TaskModel " + i + " Completed", taskModelArrayList.get(i).getCompleted());
        }
        editor.apply();
    }

    private void loadTaskModels() {
        int size = sharedPreferences.getInt("TaskModel Size", 0);
        for (int i = 0; i < size; i++) {
            String taskString = sharedPreferences.getString("TaskModel " + i + " TaskString", "");
            Boolean completed = sharedPreferences.getBoolean("TaskModel " + i + " Completed", false);
            taskModelArrayList.add(new TaskModel(taskString, completed));
        }
    }
}