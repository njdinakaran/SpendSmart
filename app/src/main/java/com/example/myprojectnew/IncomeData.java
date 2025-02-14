package com.example.myprojectnew;

public class IncomeData {

    private int amount;
    private String note;
    private String date;

    public IncomeData(int amount,String note,String date){
        this.amount=amount;
        this.date=date;
        this.note=note;
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
    public IncomeData(){

    }
}
