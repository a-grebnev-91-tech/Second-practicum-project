package tasktracker.util.csv;

import java.util.List;

public class CsvParser {
    public static void parse(String line, List<String> values) {
        if (line == null || line.isBlank()) {
            return;
        }
        int commaIndex;
        if (line.charAt(0) == '\"') {
            commaIndex = getIndexValueWithComma(line);
        } else {
            commaIndex = line.indexOf(",");
        }
        if (commaIndex > 0 && commaIndex < line.length()) {
            String value = line.substring(0, commaIndex);
            line = line.substring(commaIndex + 1);
            values.add(replaceQuotes(value));
            parse(line, values);
        } else {
            values.add(replaceQuotes(line));
        }
    }

    private static String replaceQuotes(String line) {
        if (line.contains("\"")) {
            line = line.replaceAll("\"\"", "\"");
        }
        if (line.contains(",")) {
            line = line.substring(1, line.length() - 1);
        }
        return line;
    }

    //получение индекса запятой (конец значения) в значениях где есть запятая в тексте
    private static int getIndexValueWithComma(String line) {
        if (line.isBlank()){
            return 0;
        }
        int quoteCount = 0;
        int lastQuoteIndex = 0;
        char quoteMark = '\"';
        for (int index = 0; index < line.length(); index++) {
            char currentChar = line.charAt(index);
            if (currentChar == quoteMark) {
                quoteCount++;
                lastQuoteIndex = index;
            } else if (currentChar == ',') {
                if (quoteCount % 2 == 0 && lastQuoteIndex == index - 1) {
                    return index;
                }
            }
        }
        return line.length();
    }
}
