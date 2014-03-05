package com.wlai.ModifySuffix;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Main extends Activity {

    private Button btnChooseFolder;
    private Button btnAlter;
    private Button btnRetrieve;

    private ListView lvTypes;

    private List<String> fileTypes = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SharedPreferences sp = this.getPreferences(Context.MODE_WORLD_READABLE);

        String types = sp.getString("types", null);
        initFileTypes(types);

        btnChooseFolder = (Button) findViewById(R.id.btnChooseFolder);
        btnAlter = (Button) findViewById(R.id.btnAlter);
        btnRetrieve = (Button) findViewById(R.id.btnRetrieve);
        lvTypes = (ListView) findViewById(R.id.lvTypes);

        lvTypes.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, fileTypes));
        lvTypes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        btnAlter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long[] ids = lvTypes.getCheckItemIds();
                List<String> selectedTypes = new ArrayList<String>();
                for (long id : ids) {
                    selectedTypes.add(fileTypes.get((int) id));
                }
                //TODO
            }
        });

        btnChooseFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main.this, FolderChooser.class);
                startActivityForResult(intent, 0);
            }
        });

    }

    private void initFileTypes(String types) {
        if (types == null) {
            types = "mp4,avi,rmvb,flv";
        }
        String[] ts = types.split(",");
        for (String s : ts) {
            fileTypes.add(s);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                Toast.makeText(this, data.getStringExtra("folder"), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
