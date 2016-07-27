package mksn.simple_money.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import mksn.simple_money.ActAddActivity;
import mksn.simple_money.R;
import mksn.simple_money.adapters.TransactListItemAdapter;
import mksn.simple_money.logics.AllData;
import mksn.simple_money.logics.DataBaseHelper;
import mksn.simple_money.logics.Transaction;

/**
 * Created by Mike on 07.11.2015.
 */
public class TransactListFragment extends android.support.v4.app.Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_DATA = "data_object";
    Transaction editedTransaction;
    private TransactListItemAdapter adapter = null;
    private DataBaseHelper mDataBase;
    private ExpandableListView transactListView;
    private AllData data = AllData.getInstance();

    public TransactListFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TransactListFragment newInstance(int sectionNumber) {
        TransactListFragment fragment = new TransactListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.transactListView) {
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
            int type = ExpandableListView.getPackedPositionType(info.packedPosition);
            String[] menuItems = getResources().getStringArray(R.array.context_menu_items);
            if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                menu.setHeaderTitle("Транзакция");
                for (int i = 0; i < menuItems.length; i++) {
                    menu.add(AllData.CONTEXT_MENU_TRANSACTIONS_GROUP_ID, i, i, menuItems[i]);
                }
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == AllData.CONTEXT_MENU_TRANSACTIONS_GROUP_ID) {
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item
                    .getMenuInfo();
            int type = ExpandableListView.getPackedPositionType(info.packedPosition);
            int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
            int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);
            if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                return false;
            } else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                if (item.getItemId() == 1) {    // удаление транзакции
                    mDataBase.deleteTransaction(data.getTransaction(data.getIndexFirstActInDay(groupPosition) + childPosition));
                    data.removeTransaction(data.getTransaction(data.getIndexFirstActInDay(groupPosition) + childPosition));
                    adapter.notifyDataSetChanged();
                    final WalletListFragment updatingWalFragment = (WalletListFragment)
                            getActivity().getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":1");
                    if (updatingWalFragment != null) {
                        updatingWalFragment.notifyFragment();
                    }
                } else {                        // изменение транзакции
                    Intent intent = new Intent(getActivity(), ActAddActivity.class);
                    intent.putExtra("requestCode", AllData.EDIT_TRANSACTION_REQUEST);
                    editedTransaction = data.getTransaction(data.getIndexFirstActInDay(groupPosition) + childPosition);
                    intent.putExtra(AllData.TAG_WAL_ID, data.indexOfWallet(editedTransaction.getWallet()));
                    intent.putExtra(AllData.TAG_CAT_ID, data.indexOfCategory(editedTransaction.getCategory()));
                    intent.putExtra(AllData.TAG_SUM, editedTransaction.getSum());
                    intent.putExtra(AllData.TAG_DATE, editedTransaction.getFormattedDate());
                    intent.putExtra(AllData.TAG_ACT, editedTransaction.getType());
                    intent.putExtra(AllData.TAG_CURR, editedTransaction.getWallet().getCurrency());
                    getActivity().startActivityForResult(intent, AllData.EDIT_TRANSACTION_REQUEST);
                }
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getActivity();
        if (requestCode == AllData.EDIT_TRANSACTION_REQUEST) {
            Transaction newTransaction = new Transaction(
                    editedTransaction.getId(),
                    data.getStringExtra(AllData.TAG_DATE),
                    data.getIntExtra(AllData.TAG_ACT, AllData.ACT_OUTGO),
                    data.getIntExtra(AllData.TAG_SUM, 0),
                    data.getStringExtra(AllData.TAG_CURR),
                    this.data.getWalletByID(data.getIntExtra(AllData.TAG_WAL_ID, 0)),
                    this.data.getCategoryByID(data.getIntExtra(AllData.TAG_CAT_ID, 0)));
            mDataBase.updateTransaction(newTransaction, editedTransaction);
            this.data.updateTransaction(newTransaction, editedTransaction);
            ((WalletListFragment) getActivity().getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.container + ":1"))
                    .notifyFragment();
            CategoryListFragment updatingFragment = ((CategoryListFragment) getActivity().getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.container + ":2"));
            if (updatingFragment != null) {
                updatingFragment.notifyFragment();
            }

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.transact_tab, container, false);
        transactListView = (ExpandableListView) rootView.findViewById(R.id.transactListView);
        adapter = new TransactListItemAdapter(getContext());
        mDataBase = new DataBaseHelper(getActivity().getApplicationContext());
        transactListView.setAdapter(adapter);
        registerForContextMenu(transactListView);
        for (int i = 0; i < data.getDayCount(); i++) {
            transactListView.expandGroup(i);
        }
        return rootView;
    }

    public void addTransaction(Transaction transaction) {
        data.addTransaction(transaction);
        transactListView.expandGroup(data.getDayCount() - 1);
        adapter.notifyDataSetChanged();
    }

    public void removeTransaction(Transaction transaction) {
        data.removeTransaction(transaction);
        adapter.notifyDataSetChanged();
    }

}