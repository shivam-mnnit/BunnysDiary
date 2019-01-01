package aman.agrawal.bunnysdiary.Utilities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import aman.agrawal.bunnysdiary.R;

public class CurrencyConverterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinnerfrom,spinnerto;
    private String selectedfrom="";
    private String selectedto="";
    private Double conversionfactor;
    private TextView result;
    private Button convert;
    private EditText curren;
    private static final String[] currency = {"Dollar", "Euro", "Yen", "Rupees"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        spinnerfrom = (Spinner)findViewById(R.id.currency_converter_spinnerfrom);
        spinnerto=(Spinner)findViewById(R.id.currency_converter_spinnerto);
        result=(TextView)findViewById(R.id.currencyresult);
        convert=(Button)findViewById(R.id.convert);
        curren=(EditText)findViewById(R.id.et_acceptcurrency);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CurrencyConverterActivity.this,
                                                                                android.R.layout.simple_spinner_item,currency);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerfrom.setAdapter(adapter);
        spinnerfrom.setOnItemSelectedListener(this);
        spinnerto.setAdapter(adapter);
        spinnerto.setOnItemSelectedListener(this);
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedfrom.matches("")||selectedto.matches(""))
                {
                    Toast.makeText(CurrencyConverterActivity.this, "Select currency", Toast.LENGTH_SHORT).show();
                }
                else {
                    String amount=curren.getText().toString().trim();
                    curren.getText().clear();
                    if(isDouble(amount)) {
                        Double a = Double.parseDouble(amount);
                        if (a <= 0.0) {
                            Toast.makeText(CurrencyConverterActivity.this, "Enter a positive amount", Toast.LENGTH_SHORT).show();
                        } else {
                            if (selectedfrom.matches("Dollar")) {
                                switch (selectedto) {

                                    case "Dollar":
                                        conversionfactor = 1.0;
                                        break;
                                    case "Euro":
                                        conversionfactor = 0.8498;
                                        break;
                                    case "Yen":
                                        conversionfactor = 112.8543;
                                        break;
                                    case "Rupees":
                                        conversionfactor = 72.71;
                                        break;
                                }
                            } else if (selectedfrom.matches("Euro")) {
                                switch (selectedto) {
                                    case "Dollar":
                                        conversionfactor = 1.176;
                                        break;
                                    case "Euro":
                                        conversionfactor = 1.0;
                                        break;
                                    case "Yen":
                                        conversionfactor = 132.7958;
                                        break;
                                    case "Rupees":
                                        conversionfactor = 85.567;
                                        break;
                                }
                            } else if (selectedfrom.matches("Yen")) {
                                switch (selectedto) {

                                    case "Dollar":
                                        conversionfactor = 0.0088;
                                        break;
                                    case "Euro":
                                        conversionfactor = 0.00753;
                                        break;
                                    case "Yen":
                                        conversionfactor = 1.00;
                                        break;
                                    case "Rupees":
                                        conversionfactor = 0.644;
                                        break;
                                }
                            } else {
                                switch (selectedto) {

                                    case "Dollar":
                                        conversionfactor = 0.01375;
                                        break;
                                    case "Euro":
                                        conversionfactor = 0.0116;
                                        break;
                                    case "Yen":
                                        conversionfactor = 1.5519;
                                        break;
                                    case "Rupees":
                                        conversionfactor = 1.00;
                                        break;
                                }
                            }

                            Double finalamount = a * conversionfactor;
                            String amt = String.valueOf(finalamount);
                            result.setText(amt);
                        }
                    }
                    else
                    {
                        Toast.makeText(CurrencyConverterActivity.this, "Enter amount in numeric", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {     // to select the currency type
        Spinner spinner = (Spinner) parent;
        if (spinner.getId() == R.id.currency_converter_spinnerfrom) {
            switch (position) {
                case 0:
                    selectedfrom = "Dollar";
                    break;
                case 1:
                    selectedfrom = "Euro";
                    break;
                case 2:
                    selectedfrom = "Yen";
                    break;

                case 3:
                    selectedfrom = "Rupees";
                    break;

            }
        }

        else if(spinner.getId() == R.id.currency_converter_spinnerto){
            switch (position) {
                case 0:
                    selectedto = "Dollar";
                    break;
                case 1:
                    selectedto = "Euro";
                    break;
                case 2:
                    selectedto = "Yen";
                    break;
                case 3:
                    selectedto = "Rupees";
                    break;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public boolean isDouble( String str ) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}


