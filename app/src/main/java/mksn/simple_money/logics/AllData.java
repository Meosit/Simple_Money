package mksn.simple_money.logics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Mike on 10.11.2015
 */
public class AllData {
    /**
     * Частые сокращения:
     * Act - Transaction
     * Cat - Category
     * Wal - wallet
     */
    public static final int ACT_INCOME = 102;   // типы транзакции и категории
    public static final int ACT_OUTGO = 101;
    public static final String CURR_BYR = "BUR";  // валюты
    public static final String CURR_USD = "USD";
    public static final int TYPE_ICON_CASH = 202;   // типы иконки счёта
    public static final int TYPE_ICON_CARD = 201;
    public static final int ADD_TRANSACTION_REQUEST = 11; // запросы Activity, нужны для определения цели вызова категории
    public static final int ADD_WALLET_REQUEST = 22;
    public static final int EDIT_TRANSACTION_REQUEST = 33;
    public static final int EDIT_WALLET_REQUEST = 44;
    public static final int CONTEXT_MENU_TRANSACTIONS_GROUP_ID = 301;  // определения обработки нажатий контекстного меню
    public static final int CONTEXT_MENU_WALLETS_GROUP_ID = 302;
    public static final int CONTEXT_MENU_CATEGORIES_GROUP_ID = 303;
    public static final String TAG_WAL_ID = "mksn.simphony_v2.wallet_index";  // тэги данных, передаваемых между Activity
    public static final String TAG_WAL_NAME = "mksn.simphony_v2.wallet_name";
    public static final String TAG_CAT_ID = "mksn.simphony_v2.category_index";
    public static final String TAG_CURR = "mksn.simphony_v2.currency";
    public static final String TAG_SUM = "mksn.simphony_v2.sum";
    public static final String TAG_DATE = "mksn.simphony_v2.date";
    public static final String TAG_ACT = "mksn.simphony_v2.act";
    public static final String TAG_TYPE = "mksn.simphony_v2.wallet_type";


    private ArrayList<Transaction> transactions = new ArrayList<>();
    private ArrayList<Wallet> wallets = new ArrayList<>();
    private ArrayList<Category> categoriesOutgo = new ArrayList<>();
    private ArrayList<Category> categoriesIncome = new ArrayList<>();
    private int[] indexesFirstActsInDay;  // для сортировки транзакций по дням
    private int dayCount;

    private static AllData instance = new AllData();
    private AllData() {
    }

    public static AllData getInstance() {
        return instance;
    }

    public void initWallets(ArrayList<Wallet> wallets) {  // нужны для инициализаци синглтона из MainActivity
        this.wallets = wallets;
    }

    public void initCategories(ArrayList<Category> categoriesIncome, ArrayList<Category> categoriesOutgo) {
        this.categoriesIncome = categoriesIncome;
        this.categoriesOutgo = categoriesOutgo;
    }

    public void initTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
        this.dayCount = initDaysCount();
        this.indexesFirstActsInDay = initIndexesFirstActsInDay();
    }

    private int[] initIndexesFirstActsInDay() {
        if (dayCount != 0) {
            int result[] = new int[dayCount];
            result[0] = 0;
            for (int i = 1, j = 1; i < transactions.size(); i++) {
                if (!transactions.get(i).getFormattedDate().equals(transactions.get(i - 1).getFormattedDate())) {
                    result[j] = i;
                    j++;
                }
            }
            return result;
        }
        return null;
    }

    private void updateIndexesFirstActsInDay() {
        indexesFirstActsInDay = initIndexesFirstActsInDay();
    }



    public int getIndexFirstActInDay(int dayIndex) {
        return indexesFirstActsInDay[dayIndex];
    }

    public int initDaysCount() {
        if (transactions.size() > 0) {
            int result = 1;
            for (int i = 1; i < transactions.size(); i++) {
                if (!transactions.get(i).getFormattedDate().equals(transactions.get(i - 1).getFormattedDate())) {
                    result++;
                }
            }
            return result;
        }
        return 0;
    }

    private void updateDayCount() {
        sortTransactions();
        dayCount = initDaysCount();
    }

    public int getDayCount() {
        return dayCount;
    }

    public int getInDayActsCount(int dayIndex) {  // количество транзакций в дне
        int result = 1;
        for (int i = indexesFirstActsInDay[dayIndex]; i < transactions.size() - 1; i++, result++) {
            if (!(transactions.get(i).getFormattedDate().equals(transactions.get(i + 1).getFormattedDate()))) {
                break;
            }
        }
        return result;
    }

    private void sortTransactions() {
        Collections.sort(transactions, new CustomComparator());
    }

    public class CustomComparator implements Comparator<Transaction> {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            int oi1 = o1.getYear() * 10000 + o1.getMonth() * 100 + o1.getDay();
            int oi2 = o2.getYear() * 10000 + o2.getMonth() * 100 + o2.getDay();
            return oi2 - oi1;
        }
    }

    public ArrayList<String> walletsToStringList() {  // для отображения списка счетов в spinner'ах
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < wallets.size(); i++) {
            result.add(wallets.get(i).getName());
        }
        return result;
    }

    public ArrayList<String> categoriesToStringList(int type) {  // для отображения списка категорий в spinner'ах
        ArrayList<String> result = new ArrayList<>();
        if (type >= AllData.ACT_INCOME) {
            for (int i = 0; i < categoriesIncome.size(); i++) {
                result.add(categoriesIncome.get(i).getName());
            }
        } else {
            for (int i = 0; i < categoriesOutgo.size(); i++) {
                result.add(categoriesOutgo.get(i).getName());
            }
        }
        return result;
    }

    public String[] getCurrenciesArr() {
        return new String[]{"BYN"};
    }

    public int indexOfWallet(Wallet wallet) {
        return wallets.indexOf(wallet);
    }

    public int indexOfCategory(Category category) {
        return (category.getCategoryType() == ACT_OUTGO) ? (categoriesOutgo.indexOf(category))
                : (categoriesIncome.indexOf(category));
    }

    public Wallet getWalletByID(int id) {
        for (int i = 0; i < wallets.size(); i++) {
            if (wallets.get(i).getId() == id) {
                return wallets.get(i);
            }
        }
        return null;
    }

    public Category getCategoryByID(int id) {
        for (int i = 0; i < categoriesOutgo.size(); i++) {
            if (categoriesOutgo.get(i).getId() == id) {
                return categoriesOutgo.get(i);
            }
        }
        for (int i = 0; i < categoriesIncome.size(); i++) {
            if (categoriesIncome.get(i).getId() == id) {
                return categoriesIncome.get(i);
            }
        }
        return null;
    }

    public ArrayList<Transaction> getDay(int dayIndex) {
        ArrayList<Transaction> result = new ArrayList<>();
        for (int i = indexesFirstActsInDay[dayIndex]; i < getInDayActsCount(dayIndex) + indexesFirstActsInDay[dayIndex]; i++) {
            result.add(transactions.get(i));
        }
        return result;
    }

    public Transaction getTransaction(int index) {
        return transactions.get(index);
    }

    public void addTransaction(Transaction transaction) {  // вставка в нужную позицию
        int index = 0;
        while (index < transactions.size() &&
                (transaction.getYear() * 10000 + transaction.getMonth() * 100 + transaction.getDay())
                        <
                        (transactions.get(index).getYear() * 10000 + transactions.get(index).getMonth() * 100 + transactions.get(index).getDay())) {
            index++;
        }
        transactions.add(index, transaction);
        updateDayCount();
        updateIndexesFirstActsInDay();
        if (transaction.getType() == ACT_OUTGO) {
            transaction.getWallet().deductRemainder(transaction.getSum());
        } else {
            transaction.getWallet().addRemainder(transaction.getSum());
        }
        transaction.getCategory().incActCount();
        transaction.getCategory().incActSum(transaction.getSum());
    }

    public void updateTransaction(Transaction newTransaction, Transaction editedTransaction) {
        if (editedTransaction.getCategory() != newTransaction.getCategory()) {
            editedTransaction.getCategory().decActCount();
            editedTransaction.getCategory().decActSum(editedTransaction.getSum());
            newTransaction.getCategory().incActCount();
            newTransaction.getCategory().incActSum(newTransaction.getSum());
        } else {
            editedTransaction.getCategory().decActSum(editedTransaction.getSum());
            editedTransaction.getCategory().incActSum(newTransaction.getSum());
        }
        if (editedTransaction.getWallet() != newTransaction.getWallet()) {
            int sumNew, sumOld;
            if (newTransaction.getType() == AllData.ACT_OUTGO) {
                sumNew = newTransaction.getWallet().getSumRemainder() - newTransaction.getSum();
            } else {
                sumNew = newTransaction.getWallet().getSumRemainder() + newTransaction.getSum();
            }
            if (editedTransaction.getType() == AllData.ACT_OUTGO) {
                sumOld = editedTransaction.getWallet().getSumRemainder() + editedTransaction.getSum();
            } else {
                sumOld = editedTransaction.getWallet().getSumRemainder() - editedTransaction.getSum();
            }
            editedTransaction.getWallet().setSumRemainder(sumOld);
            newTransaction.getWallet().setSumRemainder(sumNew);
        } else {
            int sum = editedTransaction.getWallet().getSumRemainder();
            if (newTransaction.getType() != editedTransaction.getType()) {
                if (newTransaction.getType() == AllData.ACT_OUTGO) {
                    sum = sum - newTransaction.getSum() - editedTransaction.getSum();
                } else {
                    sum = sum + newTransaction.getSum() + editedTransaction.getSum();
                }
            } else {
                if (newTransaction.getType() == AllData.ACT_OUTGO) {
                    sum = sum - newTransaction.getSum() + editedTransaction.getSum();
                } else {
                    sum = sum + newTransaction.getSum() - editedTransaction.getSum();
                }
            }
            if (newTransaction.getSum() != editedTransaction.getSum() ||
                    newTransaction.getType() != editedTransaction.getType()) {
                editedTransaction.getWallet().setSumRemainder(sum);
            }
        }
        boolean newDate = !Objects.equals(newTransaction.getFormattedDate(), editedTransaction.getFormattedDate());
        editedTransaction.setDate(newTransaction.getFormattedDate());
        editedTransaction.setSum(newTransaction.getSum());
        editedTransaction.setTransactType(newTransaction.getType());
        editedTransaction.setWallet(newTransaction.getWallet());
        editedTransaction.setCategory(newTransaction.getCategory());
        if (newDate) {
            updateDayCount();
            updateIndexesFirstActsInDay();
        }
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
        updateDayCount();
        updateIndexesFirstActsInDay();
        if (transaction.getType() == ACT_OUTGO) {
            transaction.getWallet().addRemainder(transaction.getSum());
        } else {
            transaction.getWallet().deductRemainder(transaction.getSum());
        }
        transaction.getCategory().decActCount();
        transaction.getCategory().decActSum(transaction.getSum());
    }

    public int getActsCount() {
        return transactions.size();
    }

    public Wallet getWallet(int index) {
        return wallets.get(index);
    }

    public void addWallet(Wallet wallet) {
        wallets.add(0, wallet);
    }

    public void removeWallet(int index) {
        wallets.remove(index);
    }

    public int getWalletsCount() {
        return wallets.size();
    }

    public Category getCategory(int index, int type) {
        if (type >= AllData.ACT_INCOME) {
            return categoriesIncome.get(index);
        } else {
            return categoriesOutgo.get(index);
        }
    }

    public int getCategoriesCount(int type) {
        if (type >= AllData.ACT_INCOME) {
            return categoriesIncome.size();
        } else {
            return categoriesOutgo.size();
        }
    }

    public void addCategory(Category category) {
        if (category.getCategoryType() >= AllData.ACT_INCOME) {
            categoriesIncome.add(category);
        } else {
            categoriesOutgo.add(category);
        }
    }

    public void removeCategory(int index, int type) {
        if (type >= AllData.ACT_INCOME) {
            categoriesIncome.remove(index);
        } else {
            categoriesOutgo.remove(index);
        }
    }

    public String getCategoryPercent(int position, int type) {  // процент всей суммы, который приходится на данную категорию
        if (type == ACT_OUTGO) {
            int allSum = 0;
            for (int i = 0; i < categoriesOutgo.size(); i++) {
                allSum += categoriesOutgo.get(i).getActSum();
            }
            double result = ((categoriesOutgo.get(position).getActSum() * 1.0 / allSum) * 100);
            if (Double.isNaN(result)) {
                return "0.0%";
            }
            return String.format("%.1f", result) + "%";
        } else {
            int allSum = 0;
            for (int i = 0; i < categoriesIncome.size(); i++) {
                allSum += categoriesIncome.get(i).getActSum();
            }

            double result = ((categoriesIncome.get(position).getActSum() * 1.0 / allSum) * 100);
            if (Double.isNaN(result)) {
                return "0.0%";
            }
            return String.format("%.1f", result) + "%";
        }
    }

    public String getMidSum() {
        double midsum = 0;
        Calendar calendar = Calendar.getInstance();
        for (Wallet wallet: wallets) {
            midsum += wallet.getSumRemainder();
        }
        midsum /= (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH) + 1);
        midsum /= 10000;
        return String.format(Locale.US,"%.2f", midsum) + " BYN";
    }

    public String getAllSum() {
        double allsum = 0;
        for (Wallet wallet: wallets) {
            allsum += wallet.getSumRemainder();
        }
        allsum /= 10000;
        return String.format(Locale.US,"%.2f",allsum) + " BYN";
    }

    public String getSumInDay(int dayIndex) {
        ArrayList<Transaction> day = getDay(dayIndex);
        double sumInDay = 0;
        for (Transaction transaction: day) {
            if(transaction.getType() == ACT_INCOME) {
                sumInDay += transaction.getSum();
            } else {
                sumInDay -= transaction.getSum();
            }

        }
        sumInDay /= 10000;
        return (sumInDay >= 0)?("+" + String.format(Locale.US, "%.2f", sumInDay)):String.format(Locale.US, "%.2f", sumInDay);
    }

}
