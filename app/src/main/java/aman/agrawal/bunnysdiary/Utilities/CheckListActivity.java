package aman.agrawal.bunnysdiary.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import aman.agrawal.bunnysdiary.R;

public class CheckListActivity extends AppCompatActivity {

    private Button btaddchecklist;
    private EditText etaddchecklist;
    private LinearLayout ll;
    private static final String PREFS_NAME = "preferenceName";
    private int temp=0;
    private StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        sb = new StringBuilder();
        btaddchecklist=(Button)findViewById(R.id.bt_addchecklist);
        etaddchecklist=(EditText)findViewById(R.id.et_addchecklist);
        ll=(LinearLayout)findViewById(R.id.linearlayoutchecklist);
        int m=ll.getChildCount();
        for(int i=0;i<m;i++) {
            CheckBox cb = (CheckBox) ((LinearLayout) ll).getChildAt(i);
            String s = cb.getText().toString();
            cb.setChecked(load(s));
        }
        SharedPreferences sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String str = sharedpreferences.getString("Custom_List",  "Charger");
        String[] names = str.split(",");
        if(names[0]!="charger") {
            for (int i = 0; i < names.length; i++) {
                CheckBox cb = new CheckBox(getApplicationContext());
                cb.setText(names[i]);
                cb.setChecked(load(names[i]));
                ll.addView(cb);
            }
        }
        btaddchecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etaddchecklist.getText().toString().trim();
                if (text.matches("")) {
                    Toast.makeText(CheckListActivity.this, "Enter item", Toast.LENGTH_SHORT).show();
                } else {
                    CheckBox cb = new CheckBox(getApplicationContext());
                    cb.setText(text);
                    cb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    temp++;
                    cb.setId(temp);
                    save(cb.isChecked(), text);
                    sb.append(text).append(",");
                    ll.addView(cb);
                    etaddchecklist.setText("");
                }
            }
        });
    }

    private void save(final boolean isChecked, String key) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }
    private boolean load(String key) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }
    public void onBackPressed(){
        SharedPreferences sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String str = sharedpreferences.getString("Custom_List",  "Charger");
        str=str+sb;
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("Custom_List",str);
        editor.apply();
        for(int i=0;i<ll.getChildCount();i++)
        {
            CheckBox cb = (CheckBox)((LinearLayout )ll).getChildAt(i);
            String s= cb.getText().toString();
            save(cb.isChecked(),s);
        }
        super.onBackPressed();
    }
}
