package com.cliffcrosland.descentparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cliff on 5/29/2015.
 */
public class Tokens {
    private List<String> tokens = new ArrayList<String>();

    private Tokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public static Tokens tokenize(String str) {
        Pattern p = Pattern.compile("([\\(\\)\\+\\-\\*/=])|(\\d+\\.?\\d*)|[\\w\\d]+|(\\s+)");
        Matcher m = p.matcher(str);
        List<String> tokens = new ArrayList<String>();
        int totalTokenLength = 0;
        while (m.find()) {
            String token = m.group();
            totalTokenLength += token.length();
            if (token.trim().length() == 0) continue;
            tokens.add(token);
        }
        if (totalTokenLength != str.length()) {
            throw new RuntimeException("Invalid expression. Some tokens were unrecognized.");
        }
        return new Tokens(tokens);
    }

    public void saveToken(String token) {
        tokens.add(0, token);
    }

    public String nextToken() {
        return tokens.remove(0);
    }

    public boolean hasMoreTokens() {
        return tokens.size() > 0;
    }

    public static boolean isOperator(String token) {
        String[] operators = new String[] { "+", "-", "*", "/", "=" };
        for (String op : operators) {
            if (op.equals(token)) return true;
        }
        return false;
    }

    public static boolean isValue(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isIdentifier(String token) {
        Pattern p = Pattern.compile("\\w[\\w\\d]*");
        Matcher m = p.matcher(token);
        m.find();
        String match = m.group();
        return token.equals(match);
    }

    public static int precedence(String operator) {
        String[] small = new String[] { "=" };
        String[] medium = new String[] { "+", "-" };
        String[] big = new String[] { "*", "/" };
        for (String op : small) {
            if (op.equals(operator)) return 1;
        }
        for (String op : medium) {
            if (op.equals(operator)) return 2;
        }
        for (String op : big) {
            if (op.equals(operator)) return 3;
        }
        return 0;
    }
}
