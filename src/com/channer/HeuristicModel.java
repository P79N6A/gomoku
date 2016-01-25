package com.channer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by channerduan on 1/2/16.
 */
public class HeuristicModel {

    public static final int WIN_VALUE = 3000000;
    public static final int THRESHOLD_OF_WIN = WIN_VALUE / 2;

    public static final boolean isWin(int value, boolean isBlackSearch) {
        if (isBlackSearch) {
            if (value > THRESHOLD_OF_WIN) return true;
        } else {
            if (value < -THRESHOLD_OF_WIN) return true;
        }
        return false;
    }

    private static Map<String, Integer> SKILL_MAP = new HashMap<String, Integer>() {{

        put("AAAAA", WIN_VALUE);
        put("_AAAA_", WIN_VALUE / 10);

        put("AAAA_", 2500);
        put("AAA_A", 3000);
        put("AA_AA", 2600);
        put("__AAA__", 3000);   // It is a new item!

        put("AAA__", 500);
        put("_A_AA_", 800);
        put("A__AA", 600);
        put("A_A_A", 550);

        put("___AA___", 650);
        put("AA___", 150);
        put("__A_A__", 250);
        put("_A__A_", 200);

    }};

    // key heuristic knowledge
    private static Map<Integer, List<char[]>> TemplateMap = new HashMap<Integer, List<char[]>>();
    private static Map<Integer, List<Integer>> TemplateValueMap = new HashMap<Integer, List<Integer>>();

    public static void Initial(boolean showLog) {

        for (Map.Entry<String, Integer> entry : SKILL_MAP.entrySet()) {
            if (showLog)
                System.out.println(entry.getKey() + "--->" + entry.getValue());
            String original = entry.getKey();
            int value = entry.getValue();
            List<char[]> list;
            List<Integer> listValue;
            if (!TemplateMap.containsKey(original.length())) {
                list = new ArrayList<char[]>();
                TemplateMap.put(original.length(), list);
                listValue = new ArrayList<Integer>();
                TemplateValueMap.put(original.length(), listValue);
            } else {
                list = TemplateMap.get(original.length());
                listValue = TemplateValueMap.get(original.length());
            }
            String reverse = new StringBuffer(entry.getKey()).reverse().toString();
            if (reverse.compareTo(original) == 0) reverse = null;

            list.add(produceTemplate(original, true));
            listValue.add(value);
            list.add(produceTemplate(original, false));
            listValue.add(-value);

            if (reverse != null) {
                if (showLog)
                    System.out.println(reverse + "--->" + entry.getValue());
                list.add(produceTemplate(reverse, true));
                listValue.add(value);
                list.add(produceTemplate(reverse, false));
                listValue.add(-value);
            }
        }

        // test
        if (showLog) {
            print();
        }
    }

    private static void print() {
        for (Map.Entry<Integer, List<char[]>> entry : TemplateMap.entrySet()) {
            System.out.println("\n" + entry.getKey() + ":");
            List<char[]> list = getTemplate(entry.getKey());
            char[] chars;
            for (int i = 0; i < list.size(); i++) {
                chars = list.get(i);
                System.out.print("(" + i + ") ");
                for (int j = 0; j < chars.length; j++) {
                    switch (chars[j]) {
                        case State.VALUE_OF_BLACK:
                            System.out.print('A');
                            break;
                        case State.VALUE_OF_WHITE:
                            System.out.print('B');
                            break;
                        default:
                            System.out.print('_');
                            break;
                    }
                }
                System.out.println(" <" + getTemplateValue(entry.getKey(), i) + ">");
            }
        }
    }

    private static char[] produceTemplate(String original, boolean isBlack) {
        char template[] = new char[original.length()];
        char mark;
        if (isBlack) mark = State.VALUE_OF_BLACK;
        else mark = State.VALUE_OF_WHITE;
        for (int i = 0; i < original.length(); i++) {
            if (original.charAt(i) == 'A') template[i] = mark;
            else template[i] = 0;
        }
        return template;
    }

    public static List<char[]> getTemplate(int patternSize) {
        return TemplateMap.get(patternSize);
    }

    public static int getTemplateValue(int patternSize, int index) {
        return TemplateValueMap.get(patternSize).get(index);
    }

}
