package com.autoservice.domain;

public abstract class AggregateRoot<ID extends Identifier> extends Entity<ID> {

    protected AggregateRoot() {
        super();
    }

    protected AggregateRoot(final ID id) {
        super(id);
    }
}
