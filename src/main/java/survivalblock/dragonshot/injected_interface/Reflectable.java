package survivalblock.dragonshot.injected_interface;

public interface Reflectable {
    default void dragonshot$markReflected() {
        throw new UnsupportedOperationException("Injected interface");
    }

    default boolean dragonshot$hasBeenReflected() {
        throw new UnsupportedOperationException("Injected interface");
    }
}
