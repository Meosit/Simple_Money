package mksn.simple_money.logics;

import java.util.Locale;

/**
 * Created by Mike on 04.11.2015.
 */
public class Transaction {
    private Category category;
    private int id;
    private Wallet wallet;
    private int sum;
    private int transact_type;
    private String currency;
    private int day;
    private int month;
    private int year;

    public Transaction(int id, int day, int month, int year, int sum, int wallet_id, int category_id) {
        this.id = id;
        this.day = day;
        this.month = month;
        this.year = year;
        this.sum = sum;
        this.wallet = AllData.getInstance().getWalletByID(wallet_id);
        this.currency = wallet.getCurrency();
        this.category = AllData.getInstance().getCategoryByID(category_id);
        this.transact_type = category.getCategoryType();
    }

    public Transaction(int id, String date, int transact_type, int sum, String currency, Wallet wallet, Category category) {
        this.id = id;
        this.category = category;
        this.wallet = wallet;
        this.sum = sum;
        String parsedDate[] = date.split("\\.");
        this.day = Integer.parseInt(parsedDate[0]);
        this.month = Integer.parseInt(parsedDate[1]);
        this.year = Integer.parseInt(parsedDate[2]);
        if (!currency.equals(AllData.CURR_BYR) && !currency.equals(AllData.CURR_USD)) {
            this.currency = AllData.CURR_BYR;
        } else {
            this.currency = currency;
        }
        if (transact_type >= AllData.ACT_INCOME) {
            this.transact_type = AllData.ACT_INCOME;
        } else {
            this.transact_type = AllData.ACT_OUTGO;
        }
    }

    public static Transaction copy(Transaction transaction) {
        Transaction result = new Transaction(transaction.getId(),
                transaction.getFormattedDate(),
                transaction.getType(),
                transaction.getSum(),
                transaction.getWallet().getCurrency(),
                transaction.getWallet(),
                transaction.getCategory()
        );
        return result;
    }

    public int getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category value) {
        category = value;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet value) {
        wallet = value;
    }

    public String getFormattedSum() {
        Double byn_sum = sum/ 10000.0;
        StringBuilder sb_sum = new StringBuilder(String.format(Locale.US, "%.2f", byn_sum));
        /*if (sb_sum.length() > 3) {
            for (int i = sb_sum.length() - 3; i > 0; i -= 3) {
                sb_sum.insert(i, " ");
            }
        }*/
        switch (currency) {
            case AllData.CURR_BYR:
                sb_sum.append(" Руб");
                break;
            case AllData.CURR_USD:
                sb_sum.append("$");
                break;
        }

        switch (transact_type) {
            case AllData.ACT_INCOME:
                sb_sum.insert(0, "+");
                break;
            case AllData.ACT_OUTGO:
                sb_sum.insert(0, " -");
                break;
        }
        return sb_sum.toString();
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int value) {
        sum = value;
    }

    public int getType() {
        return transact_type;
    }

    public void setTransactType(int transact_type) {
        if (transact_type >= AllData.ACT_INCOME) {
            this.transact_type = AllData.ACT_INCOME;
        } else {
            this.transact_type = AllData.ACT_OUTGO;
        }
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public void setDate(String date) {
        String parsedDate[] = date.split("\\.");
        this.day = Integer.parseInt(parsedDate[0]);
        this.month = Integer.parseInt(parsedDate[1]);
        this.year = Integer.parseInt(parsedDate[2]);
    }

    public String getFormattedDate() {
        StringBuilder result = new StringBuilder("");

        if (day < 10) {
            result.append("0").append(day);
        } else {
            result.append(day);
        }
        result.append(".");
        if (month < 10) {
            result.append("0").append(month);
        } else {
            result.append(month);
        }
        result.append(".");
        result.append(year);
        return result.toString();
    }

}