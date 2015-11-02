package com.levor.liferpg.View;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.levor.liferpg.Adapters.TasksAdapter;
import com.levor.liferpg.Controller.LifeController;
import com.levor.liferpg.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TasksActivity extends AppCompatActivity {
    private final String SKILLS_FILE_NAME = "skills_file_name.txt";
    private final String CHARACTERISTICS_FILE_NAME = "characteristics_file_name.txt";
    private final String TASKS_FILE_NAME = "tasks_file_name.txt";
    private final String TAG = "com.levor.liferpg";
    public final static int ADD_TASK_ACTIVITY_REQUEST_CODE = 0;

    private String skillsFromFile;
    private String characteristicsFromFile;
    private String tasksFromFile;

    private Button openSkillsButton;
    private Button openCharacteristicsButton;
    private ListView listView;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] activities;

    private final LifeController lifeController = LifeController.getInstance();
    private TasksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        openSkillsButton = (Button) findViewById(R.id.openSkillsButton);
        openCharacteristicsButton = (Button) findViewById(R.id.openCharacteristicsButton);
        listView = (ListView) findViewById(R.id.listViewTasks);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        activities = getResources().getStringArray(R.array.activities_array);

        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, activities));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(TasksActivity.this, activities[position], Toast.LENGTH_SHORT).show();
            }
        });


        readContentStringsFromFiles();
        createAdapter();
        setupListView();
        registerButtonsListeners();
    }

    @Override
    protected void onPause() {
        writeContentStringsToFile();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_new_task) {
            startActivityForResult(new Intent(TasksActivity.this, AddTaskActivity.class), ADD_TASK_ACTIVITY_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerButtonsListeners(){
        openCharacteristicsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TasksActivity.this, CharacteristicActivity.class);
                startActivity(intent);
            }
        });

        openSkillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TasksActivity.this, SkillsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ADD_TASK_ACTIVITY_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    createAdapter();
                }
                break;
            case DetailedTaskActivity.DETAILED_TASK_ACTIVITY_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    createAdapter();
                }
            default:
                //do nothing
        }
    }

    private void setupListView(){
        TasksAdapter adapter = new TasksAdapter(this, lifeController.getTasksTitlesAsList());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTaskTitle = lifeController.getTasksTitlesAsList().get(position);
                Intent intent = new Intent(TasksActivity.this, DetailedTaskActivity.class);
                intent.putExtra(DetailedTaskActivity.SELECTED_TASK_TITLE_TAG, selectedTaskTitle);
                startActivityForResult(intent, DetailedTaskActivity.DETAILED_TASK_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    private void readContentStringsFromFiles(){
        characteristicsFromFile = getStringFromFile(CHARACTERISTICS_FILE_NAME);
        skillsFromFile = getStringFromFile(SKILLS_FILE_NAME);
        tasksFromFile = getStringFromFile(TASKS_FILE_NAME);
        Log.e(TAG, "chars: " + characteristicsFromFile + "\nskiils: " + skillsFromFile + "\nTasks: " + tasksFromFile);
        lifeController.updateCurrentContentWithStrings(characteristicsFromFile, skillsFromFile, tasksFromFile);
        createAdapter();
    }

    private String getStringFromFile(String fileName){
        try{
            FileInputStream fis = openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            fis.close();
            return sb.toString();
        } catch (FileNotFoundException e){
            e.printStackTrace();
            return "";
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    private void writeContentStringsToFile(){
        writeStringToFile(lifeController.getCurrentCharacteristicsString(), CHARACTERISTICS_FILE_NAME);
        writeStringToFile(lifeController.getCurrentSkillsString(), SKILLS_FILE_NAME);
        writeStringToFile(lifeController.getCurrentTasksString(), TASKS_FILE_NAME);
        Log.d(TAG, "content saved to filesystem");
    }

    private void writeStringToFile(String str, String fileName){
        try{
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(str.getBytes());
            fos.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void createAdapter(){
        adapter = new TasksAdapter(this, lifeController.getTasksTitlesAsList());
        listView.setAdapter(adapter);
    }
}
