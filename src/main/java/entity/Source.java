package entity;

/**
 * Source Entity.
 */
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
        final boolean result;
        if (this == obj) {
            result = true;
        }
        else if (obj == null || getClass() != obj.getClass()) {
            result = false;
        }
        else {
            final Source other = (Source) obj;
            result = name.equals(other.name);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
