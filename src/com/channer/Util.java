package com.channer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by channerduan on 1/2/16.
 */
public class Util {
    public static List<State> StateSort(List<State> list, final boolean isAscend) {
        Collections.sort(list, new Comparator<State>() {
            public int compare(State arg0, State arg1) {
                if (arg0.value > arg1.value) {
                    if (isAscend) return 1;
                    else return -1;
                } else if (arg0.value < arg1.value) {
                    if (isAscend) return -1;
                    else return 1;
                } else {
                    return 0;
                }
            }
        });
        return list;
    }
}
