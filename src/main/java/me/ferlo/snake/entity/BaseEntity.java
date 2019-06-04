package me.ferlo.snake.entity;

import me.ferlo.snake.render.BaseRenderable;

abstract class BaseEntity<T extends BaseRenderable> extends BaseRenderable<T> implements Entity {

    BaseEntity() {
    }

    BaseEntity(int priority) {
        super(priority);
    }
}
