package entity;

public class Source {
    private final String name;

    public Source(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Source other = (Source) obj;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
