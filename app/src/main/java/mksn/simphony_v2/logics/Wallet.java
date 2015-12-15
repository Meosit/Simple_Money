package mksn.simphony_v2.logics;

/**
 * Created by Mike on 07.11.2015.
 */
public class Wallet {
    private int id;
    private String name;
    private String currency;
    private int sum_remainder;
    private int type;

    public Wallet(int id, String name, String currency, int sum_remainder, int type) {
        this.id = id;
        this.name = name;
        if (!currency.equals(AllData.CURR_BYR) && !currency.equals(AllData.CURR_USD)) {
            this.currency = AllData.CURR_BYR;
        } else {
            this.currency = currency;
        }
        this.sum_remainder = sum_remainder;
        if (type >= AllData.TYPE_ICON_CASH) {
            this.type = AllData.TYPE_ICON_CASH;
        } else {
            this.type = AllData.TYPE_ICON_CARD;
        }
    }

    public int getId() {
        return id;
    }

    public int getSumRemainder() {
        return sum_remainder;
    }

    public void setSumRemainder(int sum_remainder) {
        this.sum_remainder = sum_remainder;
    }

    public String getFormattedRemainder() {
        StringBuilder sb_sum = new StringBuilder(Integer.toString(sum_remainder));
        if (sb_sum.length() > 3) {
            for (int i = sb_sum.length() - 3; i > 0; i -= 3) {
                sb_sum.insert(i, " ");
            }
        }
        switch (currency) {
            case AllData.CURR_BYR:
                sb_sum.append(" BYR");
                break;
            case AllData.CURR_USD:
                sb_sum.append(" USD");
                break;
        }
        return sb_sum.toString();
    }

    public void deductRemainder(int subtrahend) {
        sum_remainder -= subtrahend;
    }

    public void addRemainder(int summand) {
        sum_remainder += summand;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        if (!currency.equals(AllData.CURR_BYR) && !currency.equals(AllData.CURR_USD)) {
            this.currency = AllData.CURR_BYR;
        } else {
            this.currency = currency;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
