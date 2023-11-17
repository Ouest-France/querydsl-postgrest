package fr.ouestfrance.querydsl.postgrest.model;

/**
 * Interface to describe pageable criteria
 */
public interface Pageable {

    /**
     * Create a simple pageRequest with size and no declarative sort
     * @param pageSize number of element in one page
     * @return pageable object
     */
    static Pageable ofSize(int pageSize) {
        return ofSize(pageSize, null);
    }

    /**
     * Create a simple pageRequest with size and declarative sort
     * @param pageSize number of element in one page
     * @param sort sort information
     * @return pageable object
     */
    static Pageable ofSize(int pageSize, Sort sort) {
        return new PageRequest(0, pageSize, sort);
    }

    /**
     * Create an un paged
     * @return pageable object
     */
    static Pageable unPaged() {
        return new PageRequest(0, 0, null);
    }

    /**
     * Request page size
     * @return page size
     */
    int getPageSize();

    /**
     * Request page number
     * @return page number
     */
    int getPageNumber();

    /**
     * Request sort
     * @return sort
     */
    Sort getSort();

    /**
     * Transform a Pageable to range representation
     * @return transform a pagination item to range value
     */
    default String toRange() {
        return pageOffset() + "-" + pageLimit();
    }


    /**
     * Calculate the page offset (index of the first item)
     *
     * @return page offset
     */
    default int pageOffset() {
        return getPageNumber() * getPageSize();
    }

    /**
     * Calculate the page limit (index of the last item)
     *
     * @return page limit
     */
    default int pageLimit() {
        return pageOffset() + getPageSize() - 1;
    }


}
