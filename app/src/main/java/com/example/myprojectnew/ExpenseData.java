package com.example.myprojectnew;

public class ExpenseData {

    private int amount;
    private String note;
    private String date;
    private String category;

    public ExpenseData(int amount, String note, String category, String date) {
        this.amount = amount;
        this.note = note;
        this.category = category;
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ExpenseData() {

    }


}
