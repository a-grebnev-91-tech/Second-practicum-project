package tasktracker.manager.util.csv;

public class CsvConstructor {
    public static String constructLine(String... values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            if (value.contains("\"")) {
                value = value.replaceAll("\"", "\"\"");
            }
            if (value.contains(",")) {
                builder.append("\"");
                builder.append(value);
                builder.append("\"");
                builder.append(",");
            } else {
                builder.append(value);
                builder.append(",");
            }
        }
        builder.append(System.lineSeparator());
        return builder.toString();
    }
}
