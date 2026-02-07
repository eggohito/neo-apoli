package io.github.eggohito.neo_apoli.context.visitor;

public interface Visitor<T> {

	boolean contains(T element);

	boolean push(T element);

	void pop(T element);

}
