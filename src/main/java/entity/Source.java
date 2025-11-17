package entity;

public class Source {
    private final String name;
    private Category category;

    public Source(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {return name;
    }
}
