package mksn.simple_money.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import mksn.simple_money.R;
import mksn.simple_money.logics.AllData;
import mksn.simple_money.logics.Transaction;

/**
 * Created by Mike on 06.11.2015.
 * Отображение элементов списка транзакций
 */
public class TransactListItemAdapter extends BaseExpandableListAdapter {

    private AllData data = AllData.getInstance();
    private Context mContext;

    public TransactListItemAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getGroupCount() {
        return data.getDayCount();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (data.getInDayActsCount(groupPosition) == 1) {
            return 1;
        } else {
            return data.getInDayActsCount(groupPosition) + 1;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.getDay(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.getTransaction(data.getIndexFirstActInDay(groupPosition) + childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.day_row, null);
        }
        TextView textGroup = (TextView) convertView.findViewById(R.id.day_header);
        textGroup.setText(data.getTransaction(data.getIndexFirstActInDay(groupPosition)).getFormattedDate());

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if ((getChildrenCount(groupPosition) == 1) || !isLastChild) {
            convertView = inflater.inflate(R.layout.transact_row, null);
            convertView.setLongClickable(true);

            TextView walletText = (TextView) convertView.findViewById(R.id.wallet_field);
            TextView categoryText = (TextView) convertView.findViewById(R.id.category_field);
            TextView sumText = (TextView) convertView.findViewById(R.id.sum_field);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon_field);

            walletText.setText(((Transaction) getChild(groupPosition, childPosition)).getCategory().getName());
            categoryText.setText(((Transaction) getChild(groupPosition, childPosition)).getWallet().getName());
            sumText.setText(((Transaction) getChild(groupPosition, childPosition)).getFormattedSum());
            if (((Transaction) getChild(groupPosition, childPosition)).getType() == AllData.ACT_INCOME) {
                icon.setImageBitmap(BitmapFactory.decodeResource(parent.getResources(), R.drawable.ic_income2));
            } else {
                icon.setImageBitmap(BitmapFactory.decodeResource(parent.getResources(), R.drawable.ic_outgo2));
            }

            return convertView;
        } else {
            convertView = inflater.inflate(R.layout.day_footer, null);
            convertView.setLongClickable(false);

            TextView dailySum = (TextView) convertView.findViewById(R.id.dailySumText);
            dailySum.setText("Всего: " + data.getSumInDay(groupPosition));
            return convertView;
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
