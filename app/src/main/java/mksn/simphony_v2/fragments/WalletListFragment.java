package mksn.simphony_v2.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import mksn.simphony_v2.R;
import mksn.simphony_v2.WalAddActivity;
import mksn.simphony_v2.adapters.WalletListItemAdapter;
import mksn.simphony_v2.logics.AllData;
import mksn.simphony_v2.logics.DataBaseHelper;
import mksn.simphony_v2.logics.Wallet;

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
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == AllData.CONTEXT_MENU_WALLETS_GROUP_ID) {
            final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int menuItemIndex = item.getItemId();
            if (menuItemIndex == 1) {
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Удаление счёта")
                        .setMessage("Вы уверены, что хотите удалить счёт? Все транзакции, связанные с этим счётом, будут удалены.")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = 0; i < data.getActsCount(); ) {
                                    if (data.getTransaction(i).getWallet() == data.getWallet(info.position)) {
                                        mDataBase.deleteTransaction(data.getTransaction(i));
                                        data.removeTransaction(data.getTransaction(i));
                                    } else {
                                        i++;
                                    }
                                }
                                mDataBase.deleteWallet(data.getWallet(info.position).getId());
                                data.removeWallet(info.position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
            } else {
                Intent intent = new Intent(getActivity(), WalAddActivity.class);
                intent.putExtra("requestCode", AllData.EDIT_WALLET_REQUEST);
                editedWalPos = info.position;
                intent.putExtra(AllData.TAG_WAL_NAME, data.getWallet(info.position).getName());
                intent.putExtra(AllData.TAG_SUM, data.getWallet(info.position).getSumRemainder());
                intent.putExtra(AllData.TAG_CURR, data.getWallet(info.position).getCurrency());
                intent.putExtra(AllData.TAG_TYPE, data.getWallet(info.position).getIconType());
                getActivity().startActivityForResult(intent, AllData.EDIT_WALLET_REQUEST);
            }
            return true;
        }
        return super.onContextItemSelected(item);
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
        return rootView;
    }

    public void notifyFragment() {
        adapter.notifyDataSetChanged();
    }
}