package com.dreammzf;

public class TextCleaner {
    public static String clean(String text) {
        if (text.isEmpty()) {
            return text;
        }
        StringBuilder newText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);
            if (curr == '?') {
                if (i == text.length() - 1) {
                    newText.append(curr);
                } else {
                    char nextChar = text.charAt(i + 1);
                    if (nextChar == ' ' || !Character.isLetterOrDigit(nextChar)) {
                        newText.append(curr);
                    }
                }
            } else {
                newText.append(curr);
            }
        }
        return newText.toString();
    }
}
