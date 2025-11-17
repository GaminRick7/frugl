package entity;

public class Transaction {
    @SerializedName("date")
    private final LocalDate date;
    
    @SerializedName("source")
    private final Source source;
    
    @SerializedName("amount")
    private final double amount;


    //create transaction class with category
    public Transaction(Source source, double amount, LocalDate date) {
        this.date = date;
        this.source = source;
        this.amount = amount;

    }

    public double getAmount(){
        return this.amount;
    }

    public Source getSource(){
        return this.source;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Category getCategory(){return source.getCategory();}

}
