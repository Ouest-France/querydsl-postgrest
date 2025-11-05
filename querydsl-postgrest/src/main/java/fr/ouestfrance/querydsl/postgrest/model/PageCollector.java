package fr.ouestfrance.querydsl.postgrest.model;

import lombok.RequiredArgsConstructor;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Class to collect elements from a paginated API
 *
 * @param <T>
 */
@RequiredArgsConstructor
public class PageCollector<T> {

    private final Function<PageRequest, Page<T>> apiCallFunction;
    private final PageCollectorOptions options;

    /**
     * construct a Stream that will call the API when necessary
     *
     * @return a Stream of the resources
     */
    public Stream<T> toStream() {
        return StreamSupport.stream(
                new PageSpliterator<>(apiCallFunction, options),
                false
        );
    }

    /**
     * Options of the PageCollector
     *
     * @param pageSize the size of the page for the API call
     */
    public record PageCollectorOptions(int pageSize, Sort sort) {
        public PageCollectorOptions {
            if (pageSize <= 0) {
                throw new IllegalArgumentException("la taille de la page doit être un entier positif");
            }
        }

        /**
         * Constructor with page size
         *
         * @param pageSize the size of the page for the API call
         */
        public PageCollectorOptions(int pageSize) {
            this(pageSize, null);
        }
    }

    /**
     * The Spliterator that actually calls the API if necessary
     *
     * @param <T>
     */
    @RequiredArgsConstructor
    static class PageSpliterator<T> implements Spliterator<T> {

        /**
         * Function that calls the paginated API
         */
        private final Function<PageRequest, Page<T>> apiCallFunction;
        private final PageCollectorOptions options;
        private Page<T> currentPage;
        private int currentIndex = 0;
        private int nextPageNumber = 0;
        private long remainingElements = -1;


        @Override
        public boolean tryAdvance(Consumer<? super T> consumer) {
            if (currentPage == null || currentPage.getData().size() <= currentIndex && remainingElements > 0) {
                callApi();
            }
            if (remainingElements == 0) {
                return false;
            }
            consumer.accept(currentPage.getData().get(currentIndex++));
            remainingElements--;
            return true;
        }

        private void callApi() {
            currentPage = apiCallFunction.apply(new PageRequest(nextPageNumber, options.pageSize(), options.sort()));
            nextPageNumber++;
            currentIndex = 0;
            if (remainingElements == -1) {
                remainingElements = currentPage.getTotalElements();
            }
        }

        @Override
        public Spliterator<T> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return remainingElements == -1 ? Long.MAX_VALUE : remainingElements;
        }

        @Override
        public long getExactSizeIfKnown() {
            return remainingElements == -1 ? -1 : remainingElements;
        }

        @Override
        public int characteristics() {
            return ORDERED | NONNULL;
        }
    }
}

