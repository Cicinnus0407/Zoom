package com.cicinnus.zoom.util;

import java.util.Iterator;

/**
 * <pre>
 * author cicinnus
 * date 2018/6/14
 * </pre>
 */
public class ProperitesUtil {

    public static String join(CharSequence delimiter, Iterable tokens) {
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = tokens.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(delimiter);
                sb.append(it.next());
            }
        }
        return sb.toString();
    }
}
