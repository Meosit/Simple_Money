package mksn.simple_money.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Locale;

import mksn.simple_money.R;
import mksn.simple_money.logics.AllData;

/**
 * Created by Mike on 06.11.2015.
 * Класс для отображения списка категория
 */
public class CategoryListItemAdapter extends BaseAdapter {

    Context mContext;
    AllData data = AllData.getInstance();
    private static LayoutInflater inflater = null;
    private int type;

    public CategoryListItemAdapter(Context mContext, int type) {
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
        this.type = type;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.getCategoriesCount(type);
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.getCategory(position, type);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void setCategoryType(int type) {
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.category_row, null);
        }

        TextView categoryName = (TextView) convertView.findViewById(R.id.category_name_field);
        TextView countText = (TextView) convertView.findViewById(R.id.category_count_field);
        TextView percentText = (TextView) convertView.findViewById(R.id.category_percent_field);

        categoryName.setText(data.getCategory(position, type).getName());
        countText.setText("(" + data.getCategory(position, type).getActCount() + ")");
        percentText.setText(String.format(Locale.US, "%.2f", data.getCategory(position,type).getActSum() / 10000.0) + " (" +data.getCategoryPercent(position, type) + ")");
        return convertView;
    }
}
