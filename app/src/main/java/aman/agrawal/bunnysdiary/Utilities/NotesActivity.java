package aman.agrawal.bunnysdiary.Utilities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import aman.agrawal.bunnysdiary.R;

public class NotesActivity extends AppCompatActivity {

    private EditText title,content;
    private Button clear,save;
    ListView listItem;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    String setTitle,setContent;
    public static int x=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        title = (EditText)findViewById(R.id.title);
        title.setText("");
        content = (EditText)findViewById(R.id.content);
        content.setText("");
        clear = (Button) findViewById(R.id.clear);
        save = (Button) findViewById(R.id.save);
        listItem = (ListView)findViewById(R.id.list_item);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("");
                content.setText("");
            }
        });

        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(NotesActivity.this, android.R.layout.simple_list_item_1, arrayList);

        listItem.setAdapter(adapter);
        registerForContextMenu(listItem);

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0)
                    save.setEnabled(false);
                else
                    save.setEnabled(true);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0)
                    save.setEnabled(false);
                else
                    save.setEnabled(true);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTitle = title.getText().toString();
                setContent = content.getText().toString();
                arrayList.add(setTitle + "\n" + setContent);
                adapter.notifyDataSetChanged();
                saveStringToPreferences();
                title.setText("");
                content.setText("");
            }
        });
    }

    private void saveStringToPreferences(){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        for (int i = 0; i < adapter.getCount(); ++i){
            editor.putString(String.valueOf(i), adapter.getItem(i));
        }
        editor.apply();
    }

    public void onResume(){
        super.onResume();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        for (int i = 0;; ++i){
            final String str = preferences.getString(String.valueOf(i), "");
            if (!str.equals("")){
                adapter.add(str);
            } else {
                break; // Empty String means the default value was returned.
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu , View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,v,menuInfo);
        menu.add("Delete");
        menu.add("Edit");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);

        if(item.getTitle()=="Delete"){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            adapter.remove(adapter.getItem(info.position));
            adapter.notifyDataSetChanged();
            editor = preferences.edit();
            editor.clear().apply();
            saveStringToPreferences();
        }
        else if(item.getTitle()=="Edit"){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String[] lines = arrayList.get(info.position).split("\n",2);
            String title1 = lines[0];
            String content1 = lines[1];

            if(title.getText().toString()!="" || content.getText().toString()!=""){
                title.setText("");
                content.setText("");
            }

            title.setText(title1);
            content.setText(content1);
            adapter.remove(adapter.getItem(info.position));
            adapter.notifyDataSetChanged();
            editor = preferences.edit();
            editor.clear().apply();
            saveStringToPreferences();

            if(save.isEnabled()==false){
                save.setEnabled(true);
            }
        }
        return true;
    }
}
