package org.qualiservice.qualianon.utility;

import java.util.Optional;
import java.util.stream.Stream;


public class ExtStream {

    public static <T> Stream<T> concat(Stream<T> stream1, Stream<T> stream2, Stream<T> stream3) {
        return Stream.concat(Stream.concat(stream1, stream2), stream3);
    }

    public static <T> Stream<T> optionalStream(Optional<T> optional) {
        if (optional.isPresent()) {
            return Stream.of(optional.get());
        } else {
            return Stream.empty();
        }
    }
}
