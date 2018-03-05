package mksn.simple_money.fragments;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import mksn.simple_money.R;
import mksn.simple_money.WalAddActivity;
import mksn.simple_money.adapters.WalletListItemAdapter;
import mksn.simple_money.logics.AllData;
import mksn.simple_money.logics.DataBaseHelper;
import mksn.simple_money.logics.Wallet;

/**
 * Created by Mike on 07.11.2015.
 */
public class WalletListFragment extends android.support.v4.app.Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    int editedWalPos;
    private AllData data = AllData.getInstance();
    private WalletListItemAdapter adapter = null;
    private DataBaseHelper mDataBase;

    public WalletListFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WalletListFragment newInstance(int sectionNumber) {
        WalletListFragment fragment = new WalletListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.walletListView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(data.getWallet(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.context_menu_items);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(AllData.CONTEXT_MENU_WALLETS_GROUP_ID, i, i, menuItems[i]);
            }
            if (data.getWalletsCount() != 1) {
                menu.add(AllData.CONTEXT_MENU_WALLETS_GROUP_ID, 2, 2, "Перенести сумму на другой счёт");
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == AllData.CONTEXT_MENU_WALLETS_GROUP_ID) {
            final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int menuItemIndex = item.getItemId();
            switch (menuItemIndex) {
                case 0:
                    editWallet(info.position);
                    break;
                case 1:
                    deleteWallet(info.position);
                    break;
                case 2:
                    midWalletTransfer(info.position);
                    break;
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void editWallet(final int pos) {
        Intent intent = new Intent(getActivity(), WalAddActivity.class);
        intent.putExtra("requestCode", AllData.EDIT_WALLET_REQUEST);
        editedWalPos = pos;
        intent.putExtra(AllData.TAG_WAL_NAME, data.getWallet(pos).getName());
        intent.putExtra(AllData.TAG_SUM, data.getWallet(pos).getSumRemainder());
        intent.putExtra(AllData.TAG_CURR, data.getWallet(pos).getCurrency());
        intent.putExtra(AllData.TAG_TYPE, data.getWallet(pos).getIconType());
        getActivity().startActivityForResult(intent, AllData.EDIT_WALLET_REQUEST);
    }

    private void deleteWallet(final int pos) {
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Удаление счёта")
                .setMessage("Вы уверены, что хотите удалить счёт? Все транзакции, связанные с этим счётом, будут удалены.")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < data.getActsCount(); ) {
                            if (data.getTransaction(i).getWallet() == data.getWallet(pos)) {
                                mDataBase.deleteTransaction(data.getTransaction(i));
                                data.removeTransaction(data.getTransaction(i));
                            } else {
                                i++;
                            }
                        }
                        mDataBase.deleteWallet(data.getWallet(pos).getId());
                        data.removeWallet(pos);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
        if (getView() != null) {
            TextView midText = (TextView) getView().findViewById(R.id.midsumText);
            midText.setText(this.data.getMidSum());
            TextView allText = (TextView) getView().findViewById(R.id.allsumText);
            allText.setText(this.data.getAllSum());
        }
    }

    private void midWalletTransfer(final int pos) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.mid_wallet_row, null);
        android.app.AlertDialog.Builder mDialogBuilder = new android.app.AlertDialog.Builder(getContext());
        mDialogBuilder.setView(promptsView);
        mDialogBuilder.setView(promptsView);
        final EditText mid_sum = (EditText) promptsView.findViewById(R.id.mid_wallet_sum);
        final TextView mid_curr = (TextView) promptsView.findViewById(R.id.mid_wallet_curr);
        mid_curr.setText(data.getWallet(pos).getCurrency());
        final Spinner mid_wallet_spinner = (Spinner) promptsView.findViewById(R.id.mid_wallet_spinner);
        final ArrayAdapter<String> category_adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), // настройка spinner'а
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
        mid_wallet_spinner.setAdapter(category_adapter);
        // индекс выбранной категории (массив, потому что во внутренних
        // классах можно использовать только финальные переменные из внешних
        // классов, но final int изменить в ходе программы нельзя)
        final int[] wallet_index = {0};
        mid_wallet_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                wallet_index[0] = position;                                   // взятие индекса выбранной категории
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Переместить",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Wallet firstEditedWallet = data.getWallet(pos);
                                Wallet secondEditedWallet = data.getWallet(wallet_index[0]);
                                if (pos != wallet_index[0]) {
                                    if (firstEditedWallet.getCurrency().equals(secondEditedWallet.getCurrency())) {
                                        int remittanceSum = (int) (Double.parseDouble(mid_sum.getText().toString()) * 10_000);
                                        firstEditedWallet.deductRemainder(remittanceSum);
                                        secondEditedWallet.addRemainder(remittanceSum);
                                        mDataBase.updateWallet(firstEditedWallet.getId(),
                                                firstEditedWallet.getName(),
                                                firstEditedWallet.getCurrency(),
                                                firstEditedWallet.getSumRemainder(),
                                                firstEditedWallet.getIconType());
                                        mDataBase.updateWallet(secondEditedWallet.getId(),
                                                secondEditedWallet.getName(),
                                                secondEditedWallet.getCurrency(),
                                                secondEditedWallet.getSumRemainder(),
                                                secondEditedWallet.getIconType());
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Toast notCurrEqualMessage = Toast.makeText(getContext(), "Нельзя переместить сумму на счёт с другой валютой", Toast.LENGTH_LONG);
                                        notCurrEqualMessage.show();
                                    }
                                    if (getView() != null) {
                                        TextView midText = (TextView) getView().findViewById(R.id.midsumText);
                                        midText.setText(WalletListFragment.this.data.getMidSum());
                                        TextView allText = (TextView) getView().findViewById(R.id.allsumText);
                                        allText.setText(WalletListFragment.this.data.getAllSum());
                                    }
                                }
                            }
                        })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getActivity();
        if (requestCode == AllData.EDIT_WALLET_REQUEST) {
            Wallet editedWallet = this.data.getWallet(editedWalPos);
            editedWallet.setName(data.getStringExtra(AllData.TAG_WAL_NAME));
            editedWallet.setSumRemainder(data.getIntExtra(AllData.TAG_SUM, 0));
            editedWallet.setCurrency(data.getStringExtra(AllData.TAG_CURR));
            editedWallet.setType(data.getIntExtra(AllData.TAG_TYPE, AllData.TYPE_ICON_CASH));
            mDataBase.updateWallet(editedWallet.getId(),
                    editedWallet.getName(),
                    editedWallet.getCurrency(),
                    editedWallet.getSumRemainder(),
                    editedWallet.getIconType());
            adapter.notifyDataSetChanged();

            if (getView() != null) {
                TextView midText = (TextView) getView().findViewById(R.id.midsumText);
                midText.setText(this.data.getMidSum());
                TextView allText = (TextView) getView().findViewById(R.id.allsumText);
                allText.setText(this.data.getAllSum());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDataBase = new DataBaseHelper(getActivity().getApplicationContext());
        View rootView = inflater.inflate(R.layout.wallet_tab, container, false);
        ListView walletListView = (ListView) rootView.findViewById(R.id.walletListView);
        registerForContextMenu(walletListView);
        adapter = new WalletListItemAdapter(getContext());
        walletListView.setAdapter(adapter);
        TextView midText = (TextView) rootView.findViewById(R.id.midsumText);
        midText.setText(this.data.getMidSum());
        TextView allText = (TextView) rootView.findViewById(R.id.allsumText);
        allText.setText(this.data.getAllSum());
        return rootView;
    }

    public void notifyFragment() {
        adapter.notifyDataSetChanged();
        if (getView() != null) {
            TextView midText = (TextView) getView().findViewById(R.id.midsumText);
            midText.setText(this.data.getMidSum());
            TextView allText = (TextView) getView().findViewById(R.id.allsumText);
            allText.setText(this.data.getAllSum());
        }
    }
}