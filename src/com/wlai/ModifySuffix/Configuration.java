package com.wlai.ModifySuffix;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User: wlai
 * Date: 3/13/14
 * Time: 10:51 AM
 */
public class Configuration extends Activity {
    private EditText editText;
    private Button button;
    private ListView listView;

    private List<String> typesList = new ArrayList<String>();
    ArrayAdapter<String> adapter = null;
    SharedPreferences sharedPreferences = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure);

        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        listView = (ListView) findViewById(R.id.listView);

        initListView();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = editText.getText().toString();
                if ("".equals(type) || type.contains(".") || type.contains(";")) {
                    Toast.makeText(Configuration.this, "名称不能有'.'或';'或为空", Toast.LENGTH_SHORT).show();
                } else {
                    // adapter.add(type);
                    typesList.add(type);
                    adapter.notifyDataSetChanged();
                    editText.setText("");
                    saveData();
                }
            }
        });


    }

    public void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("types", joinList(typesList));
        editor.commit();
    }

    public boolean onContextItemSelected(MenuItem aItem) {
        ContextMenu.ContextMenuInfo menuInfo = (ContextMenu.ContextMenuInfo) aItem.getMenuInfo();

         /* Switch on the ID of the item, to get what the user selected. */
        switch (aItem.getItemId()) {
            case 0:
                   /* Get the selected item out of the Adapter by its position. */
                //  Toast.makeText(menuInfo.)
                return true; /* true means: "we handled the event". */
        }
        return false;
    }

    public void initListView() {

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int idx, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Configuration.this);
                builder.setMessage("删除");
                builder.setTitle("提示");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String item = adapter.getItem(idx);
                        adapter.remove(item);
                        adapter.notifyDataSetChanged();
                        saveData();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
                return false;
            }
        });

        sharedPreferences = this.getSharedPreferences("types", Context.MODE_PRIVATE);

        String types = sharedPreferences.getString("types", null);
        if (types != null && !"".equals(types)) {
            String[] ts = types.split(";");
            typesList.clear();
            for (String s : ts) {
                typesList.add(s);
            }
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, typesList);
        listView.setAdapter(adapter);
    }

    public String joinList(List<String> list) {
        StringBuilder ret = new StringBuilder();
        if (list.size() != 0) {
            for (String s : list) {
                ret.append(s);
                ret.append(";");
            }
            ret.deleteCharAt(ret.lastIndexOf(";"));
        }
        return ret.toString();
    }
}