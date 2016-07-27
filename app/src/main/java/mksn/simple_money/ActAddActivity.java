package mksn.simple_money;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import mksn.simple_money.fragments.DatePickerFragment;
import mksn.simple_money.logics.AllData;
import mksn.simple_money.logics.Category;
import mksn.simple_money.logics.DataBaseHelper;
import mksn.simple_money.logics.Wallet;


public class ActAddActivity extends AppCompatActivity {

    private final AllData data = AllData.getInstance();
    int wallet_index = 0;
    int category_index = 0;
    String date;
    String currency = data.getWallet(0).getCurrency();
    private Calendar c = Calendar.getInstance();
    private EditText dateText;
    private ArrayAdapter<String> wallet_adapter;
    private ArrayAdapter<String> category_adapter;
    private Spinner category_spinner;
    private DataBaseHelper mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_add);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_add);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDataBase = new DataBaseHelper(getApplicationContext());

        // Get reference of SpinnerView from layout/main_activity.xml
        Spinner wallet_spinner = (Spinner) findViewById(R.id.wallet_spinner);
        wallet_adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line, data.walletsToStringList()) {

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
        wallet_spinner.setAdapter(wallet_adapter);

        wallet_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                currency = data.getWallet(position).getCurrency();
                TextView curr_label = (TextView) findViewById(R.id.currency_label);
                curr_label.setText("BYN");
                wallet_index = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        category_spinner = (Spinner) findViewById(R.id.category_spinner);
        category_adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line, data.categoriesToStringList(AllData.ACT_OUTGO)) {

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
        category_spinner.setAdapter(category_adapter);

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                category_index = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.act_type_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.outgo_rbtn) {
                    category_adapter.clear();
                    category_adapter.addAll(data.categoriesToStringList(AllData.ACT_OUTGO));
                } else {
                    category_adapter.clear();
                    category_adapter.addAll(data.categoriesToStringList(AllData.ACT_INCOME));
                }
            }
        });
        TextView curr_label = (TextView) findViewById(R.id.currency_label);
        dateText = (EditText) findViewById(R.id.date_container);
        int requestCode = getIntent().getIntExtra("requestCode", 0);
        if (requestCode == AllData.EDIT_TRANSACTION_REQUEST) {
            setTitle("Изменение транзакции");
            curr_label.setText(getIntent().getStringExtra(AllData.TAG_CURR));
            dateText.setText(getIntent().getStringExtra(AllData.TAG_DATE));
            date = dateText.getText().toString();
            category_spinner.setSelection(getIntent().getIntExtra(AllData.TAG_CAT_ID, 0));
            wallet_spinner.setSelection(getIntent().getIntExtra(AllData.TAG_WAL_ID, 0));
            wallet_index = getIntent().getIntExtra(AllData.TAG_WAL_ID, 0);
            category_index = getIntent().getIntExtra(AllData.TAG_CAT_ID, 0);
            ((EditText) findViewById(R.id.sumAddText)).setText(String.format(Locale.US, "%.2f", getIntent().getIntExtra(AllData.TAG_SUM, 0) / 10000.0));
            ((Button) findViewById(R.id.act_add_btn)).setText("Изменить");
            if (getIntent().getIntExtra(AllData.TAG_ACT, AllData.ACT_OUTGO) == AllData.ACT_INCOME) {
                ((RadioButton) findViewById(R.id.income_rbtn)).setChecked(true);
            }
        } else {
            setTitle(getString(R.string.add_act_activity_label));
            curr_label.setText("BYN");
            dateText.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + "." +
                    String.valueOf(c.get(Calendar.MONTH) + 1) + "." + String.valueOf(c.get(Calendar.YEAR)));
        }
    }

    public void addActBtnClick(View view) {
        EditText sumText = (EditText) findViewById(R.id.sumAddText);
        date = dateText.getText().toString();
        try {
            int sum = (int) (Double.parseDouble(sumText.getText().toString()) * 10000);
            Intent intent = new Intent();
            int act_type;
            if (((RadioButton) findViewById(R.id.outgo_rbtn)).isChecked()) {
                act_type = AllData.ACT_OUTGO;

            } else {
                act_type = AllData.ACT_INCOME;
            }
            intent.putExtra(AllData.TAG_ACT, act_type);
            intent.putExtra(AllData.TAG_SUM, sum);
            intent.putExtra(AllData.TAG_WAL_ID, data.getWallet(wallet_index).getId());
            intent.putExtra(AllData.TAG_CURR, currency);
            intent.putExtra(AllData.TAG_DATE, date);
            intent.putExtra(AllData.TAG_CAT_ID, data.getCategory(category_index, act_type).getId());
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


    public void onCalendarClick(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void walletAddClick(View view) {
        Intent intent = new Intent(ActAddActivity.this, WalAddActivity.class);
        startActivityForResult(intent, AllData.ADD_WALLET_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == AllData.ADD_WALLET_REQUEST) {
            int id = mDataBase.addWallet(data.getStringExtra(AllData.TAG_WAL_NAME),
                    data.getStringExtra(AllData.TAG_CURR),
                    data.getIntExtra(AllData.TAG_SUM, 0),
                    data.getIntExtra(AllData.TAG_TYPE, AllData.TYPE_ICON_CASH));
            this.data.addWallet(new Wallet(
                    id,
                    data.getStringExtra(AllData.TAG_WAL_NAME),
                    data.getStringExtra(AllData.TAG_CURR),
                    data.getIntExtra(AllData.TAG_SUM, 0),
                    data.getIntExtra(AllData.TAG_TYPE, AllData.TYPE_ICON_CASH)));
            wallet_adapter.insert(this.data.getWallet(0).getName(), 0);
            wallet_adapter.notifyDataSetChanged();
        }
    }

    public void categoryAddClick(View view) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.category_add, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(promptsView);
        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final EditText new_cat_name = (EditText) promptsView.findViewById(R.id.category_input_text);
        final RadioButton categoryOutgo = (RadioButton) promptsView.findViewById(R.id.cat_outgo_rbtn);
        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",

                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (categoryOutgo.isChecked()) {
                                    Category addedCategory = new Category(mDataBase.addCategory(new_cat_name.getText().toString(),
                                            AllData.ACT_OUTGO, 0, 0), new_cat_name.getText().toString(), AllData.ACT_OUTGO, 0, 0);
                                    data.addCategory(addedCategory);
                                    if (((RadioButton) findViewById(R.id.outgo_rbtn)).isChecked()) {
                                        category_adapter.add(new_cat_name.getText().toString());
                                        category_spinner.setSelection(category_adapter.getCount() - 1);
                                    }
                                } else {
                                    Category addedCategory = new Category(mDataBase.addCategory(new_cat_name.getText().toString(),
                                            AllData.ACT_INCOME, 0, 0), new_cat_name.getText().toString(), AllData.ACT_INCOME, 0, 0);
                                    data.addCategory(addedCategory);
                                    if (((RadioButton) findViewById(R.id.income_rbtn)).isChecked()) {
                                        category_adapter.add(new_cat_name.getText().toString());
                                        category_spinner.setSelection(category_adapter.getCount() - 1);
                                    }
                                }
                                category_adapter.notifyDataSetChanged();

                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = mDialogBuilder.create();

        alertDialog.show();
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
