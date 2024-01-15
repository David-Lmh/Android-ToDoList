package hk.edu.polyu.eie3109.todolist;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.myViewHolder> {
    Context context;

    ArrayList<TaskModel> taskModelArrayList;

    SharedPreferences sharedPreferences;

    public RVAdapter(Context context, ArrayList<TaskModel> taskModelArrayList) {
        this.context = context;
        this.taskModelArrayList = taskModelArrayList;
    }

    public Context getContext() {
        return context;
    }
    public static class myViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R.id.CBTask);
        }
    }

    @NonNull
    @Override
    public RVAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.task_row_layout, parent, false);

        return new RVAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapter.myViewHolder holder, int position) {
        sharedPreferences = context.getSharedPreferences("TaskModel", Context.MODE_PRIVATE);
        holder.cb.setText(taskModelArrayList.get(position).getTaskString());
        holder.cb.setChecked(taskModelArrayList.get(position).getCompleted());
        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = holder.cb.isChecked();
                taskModelArrayList.get(position).setCompleted(isChecked);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                refreshPreferences();
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskModelArrayList.size();
    }

    public void deleteItem(int position) {
        sharedPreferences = context.getSharedPreferences("TaskModel", Context.MODE_PRIVATE);
        taskModelArrayList.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, getItemCount());
        refreshPreferences();
    }

    public void editItem(int position) {
        sharedPreferences = context.getSharedPreferences("TaskModel", Context.MODE_PRIVATE);
        TaskModel taskModel = taskModelArrayList.get(position);

        Dialog editForm = new Dialog(this.context);
        editForm.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editForm.setContentView(R.layout.form_layout);

        EditText ETNewTask = editForm.findViewById(R.id.ETTaskText);
        ETNewTask.setText(taskModel.getTaskString());

        Button BNSave = editForm.findViewById(R.id.BNSubmit);
        BNSave.setText("Update");
        BNSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskModel.setTaskString(ETNewTask.getText().toString());
                editForm.dismiss();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                refreshPreferences();
                notifyDataSetChanged();
            }
        });
        editForm.show();
    }

    private void refreshPreferences() {
        sharedPreferences = context.getSharedPreferences("TaskModel", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("TaskModel Size", taskModelArrayList.size());
        for (int i = 0; i < taskModelArrayList.size(); i++) {
            editor.putString("TaskModel " + i + " TaskString", taskModelArrayList.get(i).getTaskString());
            editor.putBoolean("TaskModel " + i + " Completed", taskModelArrayList.get(i).getCompleted());
        }
        editor.apply();
    }
}