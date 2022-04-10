package org.vaadin.artur.hilla.livedb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.vaadin.flow.server.auth.AnonymousAllowed;

import org.vaadin.artur.hilla.livedb.ItemEvent.Operation;

import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Wrapped;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Endpoint
@AnonymousAllowed
public class ItemEndpoint {

    private ItemRepository itemRepository;
    private PostgresqlConnection connection;

    public ItemEndpoint(ItemRepository itemRepository, ConnectionFactory connectionFactory) {
        this.itemRepository = itemRepository;
        Wrapped<PostgresqlConnection> pooledConnection = (Wrapped<PostgresqlConnection>) Mono
                .from(connectionFactory.create()).block();
        connection = pooledConnection.unwrap();
    }

    @PostConstruct
    private void postConstruct() {
        connection.createStatement("LISTEN item_added;LISTEN item_updated;LISTEN item_deleted;").execute().subscribe();
    }

    @PreDestroy
    private void preDestroy() {
        connection.close().subscribe();
    }

    @Nonnull
    public Flux<@Nonnull Item> getItems() {
        return itemRepository.findAll();
    }

    @Nonnull
    public Flux<@Nonnull ItemEvent> getItemUpdates() {
        Flux<ItemEvent> events = connection.getNotifications().map(notification -> {
            Operation operation = Operation.forName(notification.getName());
            int id = Integer.parseInt(notification.getParameter());
            return new ItemEvent(operation, id);
        });
        Flux<ItemEvent> deleteEvents = events.filter(event -> event.getOperation() == Operation.DELETE);

        Flux<ItemEvent> eventsWithData = events.filter(event -> event.getOperation() != Operation.DELETE);
        Flux<Item> data = eventsWithData.flatMap(event -> itemRepository.findById(event.getId()));
        Flux<ItemEvent> updateInsertEvents = Flux.zip(eventsWithData, data).map(tuple -> {
            tuple.getT1().setItem(tuple.getT2());
            return tuple.getT1();
        });

        return Flux.merge(deleteEvents, updateInsertEvents);
    }
}