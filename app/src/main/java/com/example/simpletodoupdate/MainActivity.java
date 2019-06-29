package com.example.simpletodoupdate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {

    // a numeric code to identify the edit activity
    public final static int EDIT_REQUEST_CODE = 20;
    // keys used for passing data between activities
    public final static String ITEM_TEXT = "itemText";
    public final static String ITEM_POSITION = "itemposition";

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       readItems();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        lvItems = (ListView) findViewById(R.id.LvItems);
        lvItems.setAdapter(itemsAdapter);

        // mock data
       // items. add("First item");
      //  items. add("Second item");

        setupListViewListener();
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        etNewItem.setText("");
        writeItems();
        Toast.makeText(getApplicationContext(), "Item addded to List", Toast.LENGTH_SHORT).show();
    }

    private void setupListViewListener() {
        Log.i("MainActivity","setting up Listener on List View");
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id) {
                Log.i("MainActivity","Item remove from list: " + position);
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });

        // set up item listener for edit (regular click)
       lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent,View view,int positon,long id) {
               // create the new activity
               Intent i = new Intent(MainActivity.this, EditItemActivity.class);
               // pass the data being edited
               i.putExtra(ITEM_TEXT, items.get(positon));
               i.putExtra(ITEM_POSITION, positon);
               //display the activity
               startActivityForResult(i, EDIT_REQUEST_CODE);
           }
       });
    }

    // handle result from edit activity


    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //if the edit activity completed ok
        if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE ) {
            // extract updated item text from result intent extras
            String updatedITem = data.getExtras().getString(ITEM_TEXT);
            // extract Original  position of edited item
            int position = data.getExtras().getInt(ITEM_POSITION);
            // updated the model white the new item text at the edited position
            items.set(position, updatedITem);
            // notify the adapter tha the model changed
            itemsAdapter.notifyDataSetChanged();
            // persist the change model
            writeItems();
            //notify the user the operation completed ok
            Toast.makeText(this,"Item updated successfully",Toast.LENGTH_SHORT).show();
        }
    }

    private File getDataFile(){
        return new File(getFilesDir() ,"todo.txt");
    }

    private void readItems() {
        try {
        items = new ArrayList<>(FileUtils.readLines(getDataFile(),Charset.defaultCharset()));
    } catch (IOException e) {
            Log.e("MainActivity", "Error readind file", e);
            items = new ArrayList<>();
        }
    }

    private void writeItems(){
        try{
        FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.i("MainActivity","Error writing file",e);
        }
    }
}
