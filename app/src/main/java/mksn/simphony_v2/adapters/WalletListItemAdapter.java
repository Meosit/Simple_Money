package mksn.simphony_v2.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import mksn.simphony_v2.R;
import mksn.simphony_v2.logics.AllData;

/**
 * Created by Mike on 06.11.2015.
 * Класс для отображения элементов списка счетов
 */
public class WalletListItemAdapter extends BaseAdapter {

    Context mContext;
    AllData data = AllData.getInstance();
    private static LayoutInflater inflater = null;

    public WalletListItemAdapter(Context mContext) {
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.getWalletsCount();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.getWallet(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.wallet_row, null);
        }

        TextView walletText = (TextView) convertView.findViewById(R.id.wallet_name_field);
        TextView reminderText = (TextView) convertView.findViewById(R.id.remainder_field);
        ImageView icon = (ImageView) convertView.findViewById(R.id.type_icon_field);

        walletText.setText(data.getWallet(position).getName());
        reminderText.setText(data.getWallet(position).getFormattedRemainder());
        if (data.getWallet(position).getIconType() == AllData.TYPE_ICON_CARD) {  // отображение нужной иконки
            icon.setImageBitmap(BitmapFactory.decodeResource(parent.getResources(), R.drawable.ic_card));
        } else {
            icon.setImageBitmap(BitmapFactory.decodeResource(parent.getResources(), R.drawable.ic_cash));
        }

        return convertView;
    }
}
