package com.autoservice.domain;

import com.autoservice.domain.events.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<ID extends Identifier> extends Entity<ID> {

    private final List<DomainEvent> domainEvents;

    protected AggregateRoot() {
        super();
        this.domainEvents = new ArrayList<>();
    }

    protected AggregateRoot(final ID id) {
        super(id);
        this.domainEvents = new ArrayList<>();
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    protected void registerEvent(final DomainEvent event) {
        if (event == null) {
            return;
        }
        this.domainEvents.add(event);
    }

    public void clearEvents() {
        this.domainEvents.clear();
    }
}
