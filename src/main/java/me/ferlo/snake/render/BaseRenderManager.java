package me.ferlo.snake.render;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseRenderManager implements RenderManager {

    private final Map<Class<?>, Renderer<?>> rendererMap;

    public BaseRenderManager() {
        rendererMap = new HashMap<>();
    }

    protected <T> void addRendererFor(Class<T> type, Renderer<T> renderer) {
        rendererMap.put(type, renderer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Renderer<T> getRendererFor(Class<T> type) {
        return (Renderer<T>) rendererMap.get(type);
    }
}
