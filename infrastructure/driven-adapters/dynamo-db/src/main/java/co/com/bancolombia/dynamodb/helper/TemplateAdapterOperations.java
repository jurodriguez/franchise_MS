package co.com.bancolombia.dynamodb.helper;

import co.com.bancolombia.model.enums.ResponseMessage;
import co.com.bancolombia.model.exceptions.BusinessException;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.function.Function;

public abstract class TemplateAdapterOperations<E, K, V> {
    private final Class<V> dataClass;
    private final Function<V, E> toEntityFn;
    protected ObjectMapper mapper;
    private final DynamoDbAsyncTable<V> table;
    private final DynamoDbAsyncIndex<V> tableByIndex;

    @SuppressWarnings("unchecked")
    protected TemplateAdapterOperations(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                                        ObjectMapper mapper,
                                        Function<V, E> toEntityFn,
                                        String tableName,
                                        String... index) {
        this.toEntityFn = toEntityFn;
        this.mapper = mapper;
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.dataClass = (Class<V>) genericSuperclass.getActualTypeArguments()[2];
        table = dynamoDbEnhancedAsyncClient.table(tableName, TableSchema.fromBean(dataClass));
        tableByIndex = index.length > 0 ? table.index(index[0]) : null;
    }

    public Mono<E> save(E model) {
        return Mono.fromFuture(table.putItem(toEntity(model))).thenReturn(model);
    }

    public Mono<E> update(E model) {
        return Mono.fromFuture(table.updateItem(toEntity(model))).thenReturn(model);
    }

    public Mono<E> getById(K id) {
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(AttributeValue.builder().s(String.valueOf(id)).build()).build())).limit(1).build();
        return Flux.from(table.query(request))
                .flatMap(page -> Flux.fromIterable(page.items()))
                .next().map(this::toModel)
                .switchIfEmpty(Mono.error(new BusinessException(ResponseMessage.DYNAMO_DB_NOT_FOUND_RECORD)));

    }

    public <T> Mono<E> getByKeyValueAndSortKey(K partitionKey, T sortKey) {
        return Mono.fromFuture(table.getItem(Key.builder()
                .partitionValue(AttributeValue.builder()
                        .s(String.valueOf(partitionKey))
                        .build())
                .sortValue((AttributeValue.builder()
                        .s(String.valueOf(sortKey))
                        .build())).build())).map(this::toModel);
    }

    public Mono<List<E>> queryByIndex(QueryEnhancedRequest queryExpression, String... index) {
        DynamoDbAsyncIndex<V> queryIndex = index.length > 0 ? table.index(index[0]) : tableByIndex;
        SdkPublisher<Page<V>> pagePublisher = queryIndex.query(queryExpression);
        return listOfModel(pagePublisher);
    }

    private Mono<List<E>> listOfModel(SdkPublisher<Page<V>> pagePublisher) {
        return Mono.from(pagePublisher).map(page -> page.items().stream().map(this::toModel).toList());
    }

    public Mono<E> delete(E model) {
        return Mono.fromFuture(table.deleteItem(toEntity(model))).map(this::toModel);
    }

    protected V toEntity(E model) {
        return mapper.map(model, dataClass);
    }

    protected E toModel(V data) {
        return data != null ? toEntityFn.apply(data) : null;
    }

}