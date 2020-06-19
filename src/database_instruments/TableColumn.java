package database_instruments;

public class TableColumn {
    final public String name;
    final public String type;
    final public String extra;

    public TableColumn(String name, String type) {
        this.name = name;
        this.type = type;
        extra = "";
    }

    public TableColumn(String name, String type, String extra) {
        this.name = name;
        this.type = type;
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "TableColumn{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
