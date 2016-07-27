package mksn.simple_money;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import mksn.simple_money.logics.AllData;

public class WalAddActivity extends AppCompatActivity {

    private final AllData data = AllData.getInstance();
    private String currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wal_add);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_add);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final int requestCode = getIntent().getIntExtra("requestCode", AllData.ADD_WALLET_REQUEST);

        setTitle(getString(R.string.add_wal_activity_label));
        if (requestCode == AllData.EDIT_WALLET_REQUEST) {
            setTitle("Изменение счёта");
            ((Button) findViewById(R.id.wal_add_btn)).setText("Изменить");
            EditText nameText = (EditText) findViewById(R.id.wallet_name_field);
            EditText sumText = (EditText) findViewById(R.id.remainder_field);
            nameText.setText(getIntent().getStringExtra(AllData.TAG_WAL_NAME));
            sumText.setText(String.format(Locale.US, "%.2f", getIntent().getIntExtra(AllData.TAG_SUM, 0) / 10000.0));
        } else {
            setTitle(getString(R.string.add_wal_activity_label));
        }

        Spinner currency_spinner = (Spinner) findViewById(R.id.currency_spinner);
        ArrayAdapter<String> wallet_adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line, data.getCurrenciesArr()) {

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);

                return view;

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                text.setTextSize(20);
                return view;

            }
        };
        currency_spinner.setAdapter(wallet_adapter);

        currency_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == 0) {
                    currency = AllData.CURR_BYR;
                } else {
                    currency = AllData.CURR_USD;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.icon_type_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ImageView icon = (ImageView) findViewById(R.id.set_wallet_icon);
                if (checkedId == R.id.type_cash_rbtn) {
                    icon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_cash));
                } else {
                    icon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_card));
                }
            }
        });

        RadioButton typeCard = (RadioButton) findViewById(R.id.type_card_rbtn);
        if (requestCode == AllData.EDIT_WALLET_REQUEST) {
            if (getIntent().getIntExtra(AllData.TAG_TYPE, AllData.TYPE_ICON_CASH) == AllData.TYPE_ICON_CARD) {
                typeCard.setChecked(true);
            }
            if (getIntent().getStringExtra(AllData.TAG_CURR).equals(AllData.CURR_USD)) {
                currency_spinner.setSelection(1);
            }
        }
    }

    public void addWalBtnClick(View view) {
        EditText sumText = (EditText) findViewById(R.id.remainder_field);
        try {
            String wallet_name = ((EditText) findViewById(R.id.wallet_name_field)).getText().toString();
            if (wallet_name.length() == 0) {
                Toast eMsg = Toast.makeText(this, "Введите имя счёта", Toast.LENGTH_SHORT);
                eMsg.show();
                EditText wallet_text = (EditText) findViewById(R.id.wallet_name_field);
                wallet_text.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(WalAddActivity.INPUT_METHOD_SERVICE);
                imm.showSoftInput(wallet_text, InputMethodManager.SHOW_IMPLICIT);

                return;
            }
            int sum = (int) (Double.parseDouble(sumText.getText().toString()) * 10000);
            Intent intent = new Intent();
            if (((RadioButton) findViewById(R.id.type_cash_rbtn)).isChecked()) {
                intent.putExtra(AllData.TAG_TYPE, AllData.TYPE_ICON_CASH);
            } else {
                intent.putExtra(AllData.TAG_TYPE, AllData.TYPE_ICON_CARD);
            }
            intent.putExtra(AllData.TAG_WAL_NAME, wallet_name);
            intent.putExtra(AllData.TAG_SUM, sum);
            intent.putExtra(AllData.TAG_CURR, currency);
            setResult(RESULT_OK, intent);
            finish();
        } catch (NumberFormatException e) {
            Toast eMsg = Toast.makeText(this, "Введённое значение суммы недопустимо", Toast.LENGTH_SHORT);
            sumText.setText("0");
            eMsg.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
