package org.nsesa.diff.diffUtils.util;

import java.util.ListIterator;

public final class CollectionUtils {

    public final static <T> T peekNext(ListIterator<T> iterator) {
        T next = iterator.next();
        iterator.previous();
        return next;
    }

    public final static <T> T peekPrevious(ListIterator<T> iterator) {
        T previous = iterator.previous();
        iterator.next();
        return previous;
    }
}
