package com.wlai.ModifySuffix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: wlai
 * Date: 3/4/14
 * Time: 4:01 PM
 */
public class FolderChooser extends Activity {

    private ListView lvFolders;
    private TextView tvPath;
    private Button btnToParent;
    private Button btnConfirm;

    private List<String> folders = new ArrayList<String>();

    private File parentFolder;

    public FolderChooser() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foler_chooser);


        lvFolders = (ListView) findViewById(R.id.lvFolders);
        tvPath = (TextView) findViewById(R.id.tvPath);
        btnToParent = (Button) findViewById(R.id.btnToParent);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);

        parentFolder = Environment.getExternalStorageDirectory();
        initViews();

        btnToParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toParentFolder();
            }
        });

        lvFolders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String folderName=folders.get(i);
                parentFolder=new File(parentFolder,folderName);
                initViews();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("folder", parentFolder.getAbsolutePath());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    private void initViews() {
        if (parentFolder == null) btnToParent.setEnabled(false);
        else {
            btnToParent.setEnabled(true);
            tvPath.setText(parentFolder.getPath());
            listFolder(parentFolder);
            lvFolders.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, android.R.id.text1, folders));
            lvFolders.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    private void listFolder(File parent) {
        folders.clear();
        File[] files = parent.listFiles();
        for (File f : files) {
            if (f.isDirectory() && !f.isHidden()) {
                folders.add(f.getName());
            }
        }

        Collections.sort(folders,new MyComp());
    }

    public void toParentFolder() {
        parentFolder = parentFolder.getParentFile();
        initViews();
    }


}

class MyComp implements Comparator<String>{

    @Override
    public int compare(String s, String s2) {
        return s.compareTo(s2);
    }
}