package org.vaadin.artur.hilla.livedb;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ItemRepository extends ReactiveCrudRepository<Item, Integer> {
}
