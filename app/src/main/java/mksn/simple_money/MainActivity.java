package mksn.simple_money;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import mksn.simple_money.fragments.CategoryListFragment;
import mksn.simple_money.fragments.TransactListFragment;
import mksn.simple_money.fragments.WalletListFragment;
import mksn.simple_money.logics.AllData;
import mksn.simple_money.logics.Category;
import mksn.simple_money.logics.DataBaseHelper;
import mksn.simple_money.logics.Transaction;
import mksn.simple_money.logics.Wallet;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private FragmentPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private AllData data = AllData.getInstance();
    private DataBaseHelper mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mDataBase = new DataBaseHelper(getApplicationContext());
        getData().initWallets(mDataBase.getAllWallets());
        getData().initCategories(mDataBase.getAllCategories(AllData.ACT_INCOME), mDataBase.getAllCategories(AllData.ACT_OUTGO));
        getData().initTransactions(mDataBase.getAllTransactions());
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        if (data.getWalletsCount() > 0) {  // нельзя добавить транзакцию без счёта
                            Intent intent = new Intent(MainActivity.this, ActAddActivity.class);
                            intent.putExtra("requestCode", AllData.ADD_TRANSACTION_REQUEST);
                            startActivityForResult(intent, AllData.ADD_TRANSACTION_REQUEST);
                        } else {
                            Toast eMsg = Toast.makeText(getApplicationContext(), "Вы не добавили ни одного счёта.", Toast.LENGTH_SHORT);
                            eMsg.show();
                        }
                        break;
                    case 1:
                        Intent intent = new Intent(MainActivity.this, WalAddActivity.class);
                        intent.putExtra("requestCode", AllData.ADD_WALLET_REQUEST);
                        startActivityForResult(intent, AllData.ADD_WALLET_REQUEST);
                        break;
                    case 2:
                        addNewCategory();
                }
            }
        });
    }

    public AllData getData() {
        return data;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            final TransactListFragment updatingActFragment = (TransactListFragment)
                    getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":0");
            final WalletListFragment updatingWalFragment = (WalletListFragment)
                    getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":1");
            switch (requestCode) {
                case AllData.ADD_TRANSACTION_REQUEST:
                    int id = mDataBase.addTransaction(data.getStringExtra(AllData.TAG_DATE),
                            data.getIntExtra(AllData.TAG_ACT, 0),
                            data.getIntExtra(AllData.TAG_SUM, 0),
                            getData().getWalletByID(data.getIntExtra(AllData.TAG_WAL_ID, 0)),
                            getData().getCategoryByID(data.getIntExtra(AllData.TAG_CAT_ID, 0)));
                    final Transaction addedTransaction = new Transaction(
                            id,
                            data.getStringExtra(AllData.TAG_DATE),
                            data.getIntExtra(AllData.TAG_ACT, 0),
                            data.getIntExtra(AllData.TAG_SUM, 0),
                            data.getStringExtra(AllData.TAG_CURR),
                            getData().getWalletByID(data.getIntExtra(AllData.TAG_WAL_ID, 0)),
                            getData().getCategoryByID(data.getIntExtra(AllData.TAG_CAT_ID, 0)));
                    updatingActFragment.addTransaction(addedTransaction);
                    updatingWalFragment.notifyFragment();
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    Snackbar.make(findViewById(R.id.fab), "Новая транзакция добавлена.", Snackbar.LENGTH_SHORT)
                            .setAction("Отмена", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDataBase.deleteTransaction(addedTransaction);
                                    updatingActFragment.removeTransaction(addedTransaction);
                                    updatingWalFragment.notifyFragment();
                                    mSectionsPagerAdapter.notifyDataSetChanged();
                                }
                            }).show();
                    break;
                case AllData.ADD_WALLET_REQUEST:
                    if (updatingWalFragment != null) {
                        id = mDataBase.addWallet(data.getStringExtra(AllData.TAG_WAL_NAME),
                                data.getStringExtra(AllData.TAG_CURR),
                                data.getIntExtra(AllData.TAG_SUM, 0),
                                data.getIntExtra(AllData.TAG_TYPE, AllData.TYPE_ICON_CASH));
                        getData().addWallet(new Wallet(
                                id,
                                data.getStringExtra(AllData.TAG_WAL_NAME),
                                data.getStringExtra(AllData.TAG_CURR),
                                data.getIntExtra(AllData.TAG_SUM, 0),
                                data.getIntExtra(AllData.TAG_TYPE, AllData.TYPE_ICON_CASH)));
                        updatingWalFragment.notifyFragment();
                    }
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    Snackbar.make(findViewById(R.id.fab), "Новый счёт добавлен.", Snackbar.LENGTH_SHORT).show();
                    break;
                case AllData.EDIT_WALLET_REQUEST:
                    updatingWalFragment.onActivityResult(requestCode, resultCode, data);
                    break;
                case AllData.EDIT_TRANSACTION_REQUEST:
                    updatingActFragment.onActivityResult(requestCode, resultCode, data);
                    break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void addNewCategory() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.category_add, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(promptsView);

        final CategoryListFragment updatingCatFragment = (CategoryListFragment)
                getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":2");
        final RadioButton categoryOutgo = (RadioButton) findViewById(R.id.cat_outgo_filter);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Новая категория");
        alert.setMessage("Введите название:");
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (categoryOutgo.isChecked()) {
                    Category addedCategory = new Category(mDataBase.addCategory(input.getEditableText().toString(),
                            AllData.ACT_OUTGO, 0, 0), input.getEditableText().toString(), AllData.ACT_OUTGO, 0, 0);
                    data.addCategory(addedCategory);
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    updatingCatFragment.notifyFragment();
                    Snackbar.make(findViewById(R.id.fab), "Новая категория добавлена.", Snackbar.LENGTH_SHORT)
                            .setAction("Отмена", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDataBase.deleteCategory(getData()
                                            .getCategory(data.getCategoriesCount(AllData.ACT_OUTGO) - 1, AllData.ACT_OUTGO)
                                            .getId());
                                    getData().removeCategory(data.getCategoriesCount(AllData.ACT_OUTGO) - 1, AllData.ACT_OUTGO);
                                    updatingCatFragment.notifyFragment();
                                    mSectionsPagerAdapter.notifyDataSetChanged();
                                }
                            }).show();
                } else {
                    Category addedCategory = new Category(mDataBase.addCategory(input.getEditableText().toString(),
                            AllData.ACT_INCOME, 0, 0), input.getEditableText().toString(), AllData.ACT_INCOME, 0, 0);
                    data.addCategory(addedCategory);
                    mSectionsPagerAdapter.notifyDataSetChanged();
                    updatingCatFragment.notifyFragment();
                    Snackbar.make(findViewById(R.id.fab), "Новая категория добавлена.", Snackbar.LENGTH_SHORT)
                            .setAction("Отмена", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDataBase.deleteCategory(getData()
                                            .getCategory(data.getCategoriesCount(AllData.ACT_INCOME) - 1, AllData.ACT_INCOME)
                                            .getId());
                                    getData().removeCategory(data.getCategoriesCount(AllData.ACT_OUTGO) - 1, AllData.ACT_OUTGO);
                                    updatingCatFragment.notifyFragment();
                                    mSectionsPagerAdapter.notifyDataSetChanged();
                                }
                            }).show();
                }
            }
        });
        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TransactListFragment.newInstance(position + 1);
                case 1:
                    return WalletListFragment.newInstance(position + 1);
                case 2:
                    return CategoryListFragment.newInstance(position + 1);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.transaction_section_header);
                case 1:
                    return getString(R.string.wallet_section_header);
                case 2:
                    return getString(R.string.category_section_header);
            }
            return null;
        }
    }
}
