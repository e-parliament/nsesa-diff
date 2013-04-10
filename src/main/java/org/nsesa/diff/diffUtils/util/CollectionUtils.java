/**
 * Copyright 2013 European Parliament
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
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
