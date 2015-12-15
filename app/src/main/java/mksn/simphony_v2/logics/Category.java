package mksn.simphony_v2.logics;

/**
 * Created by Mike on 11.11.2015.
 */
public class Category {
    private String name;
    private int id;
    private int type;
    private int actCount;
    private int actSum;

    public Category(int id, String name, int type, int actCount, int actSum) {
        this.name = name;
        this.id = id;
        this.actCount = actCount;
        this.actSum = actSum;
        if (type >= AllData.ACT_INCOME) {
            this.type = AllData.ACT_INCOME;
        } else {
            this.type = AllData.ACT_OUTGO;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryType() {
        return type;
    }

    public int getActCount() {
        return actCount;
    }

    public int getActSum() {
        return actSum;
    }

    public void incActSum(int sum) {
        actSum += sum;
    }

    public void incActCount() {
        actCount++;
    }

    public void decActSum(int sum) {
        actSum -= sum;
    }

    public void decActCount() {
        actCount--;
    }
}
