package com.wlai.ModifySuffix;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class Main extends Activity {
    public static final String TAG = Main.class.toString();

    private Button btnChooseFolder;
    private Button btnAlter;
    private Button btnRetrieve;

    private ListView lvTypes;

    private List<String> fileTypes = new ArrayList<String>();

    private List<String> selectedFileTypes = new ArrayList<String>();

    private String selectedFolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        btnChooseFolder = (Button) findViewById(R.id.btnChooseFolder);
        btnAlter = (Button) findViewById(R.id.btnAlter);
        btnRetrieve = (Button) findViewById(R.id.btnRetrieve);
        lvTypes = (ListView) findViewById(R.id.lvTypes);


        btnAlter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long[] ids = lvTypes.getCheckItemIds();
                List<String> selectedTypes = new ArrayList<String>();
                for (long id : ids) {
                    selectedTypes.add("." + fileTypes.get((int) id));
                }
                if (selectedFolder == null) {
                    selectedFolder = Environment.getRootDirectory().getAbsolutePath();
                }
                File file = new File(selectedFolder);
                File[] files = file.listFiles();
                for (File f : files) {
                    if (f.isDirectory()) continue;
                    String name = f.getName().toLowerCase();
                    for (String suffix : selectedTypes) {
                        if (name.endsWith(suffix)) {
                            File to = new File(f.getParent(), name + ".alter");
                            Log.e(TAG, to.getAbsolutePath());
                            try {
                                boolean success = f.renameTo(to);
                                Log.e(TAG, success + "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            }
        });


        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(selectedFolder);
                File[] files = file.listFiles();
                for (File f : files) {
                    if (f.isDirectory()) continue;
                    String name = f.getName().toLowerCase();
                    Log.e(TAG,name);
                    if (name.endsWith(".alter")) {
                        File to = new File(f.getParent(), Utils.replaceLast(name, ".alter", ""));
                        Log.e(TAG, to.getAbsolutePath());
                        try {
                            boolean success = f.renameTo(to);
                            Log.e(TAG, success + "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
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

    private void initFileTypes() {

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, fileTypes);
        lvTypes.setAdapter(adapter);
        lvTypes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        fileTypes.clear();
        SharedPreferences sp = this.getSharedPreferences("types", Context.MODE_PRIVATE);
        String types = sp.getString("types", null);
        if (types == null) {
            types = "mp4;avi;rmvb;flv";
        }
        if (!"".equals(types)) {
            String[] ts = types.split(";");
            for (String s : ts) {
                fileTypes.add(s);
            }
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initFileTypes();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                selectedFolder = data.getStringExtra("folder");
                //   Toast.makeText(this, data.getStringExtra("folder"), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.suffixDefine: {
                Intent intent = new Intent(this, Configuration.class);
                startActivity(intent);
            }
        }


        return true;
    }


    private void renameTo(File f1, File f2) {
        if (canRename(f1, f2)) {
            if (!f1.renameTo(f2)) {
                Log.e(TAG, "Error to move new app: " + f1 + " > " + f2);
            }
        } else {
            try {
                copy(f1, f2);
                f1.delete();
            } catch (Exception ex) {
                Log.e(TAG, "Error to move new app: " + f1 + " > " + f2);
            }
        }
    }

    private void copy(final File f1, final File f2) throws IOException {
        f2.createNewFile();

        final RandomAccessFile file1 = new RandomAccessFile(f1, "r");
        final RandomAccessFile file2 = new RandomAccessFile(f2, "rw");

        file2.getChannel().write(file1.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, f1.length()));

        file1.close();
        file2.close();
    }

    private boolean canRename(final File f1, final File f2) {
        final String p1 = f1.getAbsolutePath().replaceAll("^(/mnt/|/)", "");
        final String p2 = f2.getAbsolutePath().replaceAll("^(/mnt/|/)", "");

        return p1.replaceAll("\\/\\w+", "").equals(p2.replaceAll("\\/\\w+", ""));
    }
}
