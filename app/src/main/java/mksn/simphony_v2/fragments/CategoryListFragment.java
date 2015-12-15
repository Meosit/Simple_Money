package mksn.simphony_v2.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import mksn.simphony_v2.R;
import mksn.simphony_v2.adapters.CategoryListItemAdapter;
import mksn.simphony_v2.logics.AllData;
import mksn.simphony_v2.logics.DataBaseHelper;
import mksn.simphony_v2.logics.Transaction;

/**
 * Created by Mike on 07.11.2015.
 * Фрагмент, отображающий категории
 */
public class CategoryListFragment extends android.support.v4.app.Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    private AllData data = AllData.getInstance();
    private CategoryListItemAdapter adapter = null;
    private DataBaseHelper mDataBase;
    private int type = AllData.ACT_OUTGO;  // тип категорий, которые отображаются в данный момент

    public CategoryListFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CategoryListFragment newInstance(int sectionNumber) {
        CategoryListFragment fragment = new CategoryListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.categoryListView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(data.getCategory(info.position, type).getName());
            String[] menuItems;
            if (info.position == 0) {
                menuItems = new String[]{"Отчистить статистику"};  // категорию 0 "Без категории" удалить и изменить нельзя
            } else {
                menuItems = new String[]{"Изменить", "Удалить", "Отчистить статистику"};
            }
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(AllData.CONTEXT_MENU_CATEGORIES_GROUP_ID, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == AllData.CONTEXT_MENU_CATEGORIES_GROUP_ID) {
            final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int menuItemIndex = item.getItemId();
            switch (menuItemIndex) {
                case 0:
                    if (info.position == 0) { // обработка для нуевой категории
                        clearStats(0);
                    } else {
                        editCategory(info.position);
                    }
                    break;
                case 1:
                    deleteCategory(info.position);
                    break;
                case 2:
                    clearStats(info.position);
                    break;
            }
            adapter.notifyDataSetChanged();  // оповещения списка катоегория и списка счетов об изменении данных
            ((WalletListFragment) getActivity().getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.container + ":1"))
                    .notifyFragment();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void editCategory(final int position) {   // изменение имени категории
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getContext());
        alert.setTitle("Изменить название");
        alert.setMessage("Введите новое название:");
        final EditText input = new EditText(getContext());
        alert.setView(input);
        alert.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                data.getCategory(position, type).setName(input.getText().toString());
                mDataBase.updateCategory(data.getCategory(position, type).getId(),
                        data.getCategory(position, type).getName(),
                        data.getCategory(position, type).getCategoryType(),
                        data.getCategory(position, type).getActCount(),
                        data.getCategory(position, type).getActSum());
                adapter.notifyDataSetChanged();
            }
        });
        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        android.app.AlertDialog alertDialog = alert.create();
        alertDialog.show();
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |   // автоматическое открытие клавиатуры
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void clearStats(final int position) {   // отчистка статистики и удаление всех транзакций категории
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Отчистить данные")
                .setMessage("Вы уверены, что хотите отчистить данные? Все транзакции, связанные с этой категорией, будут удалены.")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < data.getActsCount(); ) {
                            if (data.getTransaction(i).getCategory() == data.getCategory(position, type)) {
                                mDataBase.deleteTransaction(data.getTransaction(i));
                                data.removeTransaction(data.getTransaction(i));
                            } else {
                                i++;
                            }
                        }
                        ((WalletListFragment) getActivity().getSupportFragmentManager()
                                .findFragmentByTag("android:switcher:" + R.id.container + ":1"))
                                .notifyFragment();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void deleteCategory(final int pos) {   // удаление категории
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.category_delete, null);
        android.app.AlertDialog.Builder mDialogBuilder = new android.app.AlertDialog.Builder(getContext());
        mDialogBuilder.setView(promptsView);
        final Spinner category_spinner = (Spinner) promptsView.findViewById(R.id.cat_delete_spinner);
        final ArrayAdapter<String> category_adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), // настройка spinner'а
                android.R.layout.simple_dropdown_item_1line, data.categoriesToStringList(type)) {
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
        // индекс выбранной категории (массив, потому что во внутренних
        // классах можно использовать только финальные переменные из внешних
        // классов, но final int изменить в ходе программы нельзя)
        final int[] category_index = {0};

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == pos) {
                    category_spinner.setSelection(0);
                    category_index[0] = 0;
                    Toast cancel_massage = Toast.makeText(getContext(), "Переместить транзакции в удаляемую категорию невозможно.", Toast.LENGTH_LONG);
                    cancel_massage.show();
                } else {
                    category_index[0] = position;                                   // взятие индекса выбранной категории
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        final RadioButton cat_delete_all_rbtn = (RadioButton) promptsView.findViewById(R.id.cat_delete_all_rbtn);
        final RadioGroup radioGroup = (RadioGroup) promptsView.findViewById(R.id.cat_delete_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.cat_delete_all_rbtn) {
                    category_spinner.setEnabled(false);
                } else {
                    category_spinner.setEnabled(true);
                }
            }
        });
        category_spinner.setEnabled(false);
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Удалить",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (cat_delete_all_rbtn.isChecked()) {
                                    new AlertDialog.Builder(getContext())
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle("Отчистить данные")
                                            .setMessage("Вы уверены, что хотите удалить все транзакции, связанные с этой категорией?")
                                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    for (int i = 0; i < data.getActsCount(); ) {
                                                        if (data.getTransaction(i).getCategory() == data.getCategory(pos, type)) {
                                                            mDataBase.deleteTransaction(data.getTransaction(i));
                                                            data.removeTransaction(data.getTransaction(i));
                                                        } else {
                                                            i++;
                                                        }
                                                    }
                                                    mDataBase.deleteCategory(data.getCategory(pos, type).getId());
                                                    data.removeCategory(pos, type);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            })
                                            .setNegativeButton("Отмена", null)
                                            .show();
                                } else {
                                    for (int i = 0; i < data.getActsCount(); i++) {
                                        if (data.getTransaction(i).getCategory() == data.getCategory(pos, type)) {
                                            Transaction oldTransaction = Transaction.copy(data.getTransaction(i));
                                            data.getTransaction(i).setCategory(data.getCategory(category_index[0], type));
                                            mDataBase.updateTransaction(data.getTransaction(i), oldTransaction);
                                        }
                                    }
                                    mDataBase.deleteCategory(data.getCategory(pos, type).getId());
                                    data.removeCategory(pos, type);
                                    adapter.notifyDataSetChanged();
                                }
                                ((WalletListFragment) getActivity().getSupportFragmentManager()
                                        .findFragmentByTag("android:switcher:" + R.id.container + ":1"))
                                        .notifyFragment();
                            }
                        })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
        adapter.notifyDataSetChanged();
        ((WalletListFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.container + ":1"))
                .notifyFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDataBase = new DataBaseHelper(getActivity().getApplicationContext());  // подключение к базе данных
        View rootView = inflater.inflate(R.layout.categories_tab, container, false);
        RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.cat_filter_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.cat_outgo_filter) {
                    type = AllData.ACT_OUTGO;
                } else {
                    type = AllData.ACT_INCOME;
                }
                adapter.setCategoryType(type);
                adapter.notifyDataSetChanged();
            }
        });
        ListView categoryListView = (ListView) rootView.findViewById(R.id.categoryListView);
        registerForContextMenu(categoryListView);
        adapter = new CategoryListItemAdapter(getContext(), type);
        categoryListView.setAdapter(adapter);
        return rootView;
    }

    public void notifyFragment() {
        adapter.setCategoryType(type);
        adapter.notifyDataSetChanged();
    }
}