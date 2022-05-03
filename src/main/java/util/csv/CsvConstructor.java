package util.csv;

public class CsvConstructor {
    public static String constructLine(String... values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if (value.isBlank()) {
                continue;
            }
            if (i != 0) {
                builder.append(",");
            }
            if (value.contains("\"")) {
                value = value.replaceAll("\"", "\"\"");
            }
            if (value.contains(",")) {
                builder.append("\"");
                builder.append(value);
                builder.append("\"");
            } else {
                builder.append(value);
            }
        }
        builder.append(System.lineSeparator());
        return builder.toString();
    }
}
