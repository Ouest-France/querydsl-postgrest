package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.SelectFilter;
import fr.ouestfrance.querydsl.postgrest.services.ext.PostgrestQueryProcessorService;
import fr.ouestfrance.querydsl.postgrest.utils.FilterUtils;
import fr.ouestfrance.querydsl.service.ext.QueryDslProcessorService;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class PostgrestRpcClient {

    private static final String RPC = "rpc/";
    private final PostgrestClient client;
    private final QueryDslProcessorService<Filter> processorService = new PostgrestQueryProcessorService();

    /**
     * Execute a rpc call without body
     *
     * @param rpcName rpc name
     * @param type    class of return
     * @param <V>     type of the return object
     * @return response
     */
    public <V> V executeRpc(String rpcName, Type type) {
        return executeRpc(rpcName, null, type);
    }

    /**
     * Execute a rpc call
     *
     * @param rpcName rpc name
     * @param body    body request to send
     * @param type    class of return
     * @param <V>     type of return object
     * @return response
     */
    public <V> V executeRpc(String rpcName, Object body, Type type) {
        return executeRpc(rpcName, null, body, type);
    }


    /**
     * Execute a rpc call
     *
     * @param rpcName rpc name
     * @param body    body request to send
     * @param type    class of return
     * @param <V>     type of return object
     * @return response
     */
    public <V> V executeRpc(String rpcName, Object criteria, Object body, Type type) {
        // List filters
        List<Filter> queryParams = processorService.process(criteria);
        // Extract selection
        getSelects(criteria).ifPresent(queryParams::add);

        return client.rpc(RPC + rpcName, FilterUtils.toMap(queryParams), body, type);
    }


    /**
     * Extract selection on criteria and class
     *
     * @param criteria search criteria
     * @return attributes
     */
    private Optional<Filter> getSelects(Object criteria) {
        return Optional.of(FilterUtils.getSelectAttributes(criteria))
                .filter(x -> !x.isEmpty())
                .map(SelectFilter::only);
    }

}
