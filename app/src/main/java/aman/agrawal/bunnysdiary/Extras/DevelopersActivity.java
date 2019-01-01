package aman.agrawal.bunnysdiary.Extras;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.widget.TextView;

import aman.agrawal.bunnysdiary.R;

public class DevelopersActivity extends AppCompatActivity {

    private TextView aman,shivam,aayush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developers);

        aman= (TextView) findViewById(R.id.aman);
        aman.setMovementMethod(LinkMovementMethod.getInstance());
        aman.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        aman.setLinkTextColor(Color.parseColor("#0000e6"));

        shivam= (TextView) findViewById(R.id.shivam);
        shivam.setMovementMethod(LinkMovementMethod.getInstance());
        shivam.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        shivam.setLinkTextColor(Color.parseColor("#0000e6"));

        aayush= (TextView) findViewById(R.id.aayush);
        aayush.setMovementMethod(LinkMovementMethod.getInstance());
        aayush.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        aayush.setLinkTextColor(Color.parseColor("#0000e6"));
    }
}
