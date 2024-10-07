package fr.ouestfrance.querydsl.postgrest;

import fr.ouestfrance.querydsl.postgrest.model.Filter;
import fr.ouestfrance.querydsl.postgrest.model.impl.SelectFilter;
import fr.ouestfrance.querydsl.postgrest.services.ext.PostgrestQueryProcessorService;
import fr.ouestfrance.querydsl.postgrest.utils.FilterUtils;
import fr.ouestfrance.querydsl.service.ext.QueryDslProcessorService;
import lombok.RequiredArgsConstructor;

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
     * @param clazz   class of return
     * @param <DTO>   type of the return object
     * @return response
     */
    public <DTO> Optional<DTO> executeRpc(String rpcName, Class<DTO> clazz) {
        return executeRpc(rpcName, null, clazz);
    }

    /**
     * Execute a rpc call
     *
     * @param rpcName rpc name
     * @param body    body request to send
     * @param clazz   class of return
     * @param <DTO>   type of return object
     * @return response
     */
    public <DTO> Optional<DTO> executeRpc(String rpcName, Object body, Class<DTO> clazz) {
        return executeRpc(rpcName, null, body, clazz);
    }

    /**
     * Execute a rpc call
     *
     * @param rpcName rpc name
     * @param body    body request to send
     * @param clazz   class of return
     * @param <DTO>   type of return object
     * @return response
     */
    public <DTO> Optional<DTO> executeRpc(String rpcName, Object criteria, Object body, Class<DTO> clazz) {
        // List filters
        List<Filter> queryParams = processorService.process(criteria);
        // Extract selection
        getSelects(criteria).ifPresent(queryParams::add);
        return Optional.ofNullable(client.rpc(RPC + rpcName, FilterUtils.toMap(queryParams), body, clazz));
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
