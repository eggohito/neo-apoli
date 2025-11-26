package io.github.eggohito.neo_apoli.util.context;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.level.Level;

import java.util.Set;

@Getter
public abstract class AbstractContext<C extends Context> implements Context {

	private final ContextMap parameters;
	private final Level world;

	private ImmutableSet<ContextAware> activeEntries;
	private ContextAware.ProblemReporter reporter;

	protected AbstractContext(ContextMap.Builder parameters, Set<ContextAware> activeEntries, Level world, ContextAware.ProblemReporter reporter) {
		this.parameters = parameters.create(reporter.getKeySet());
		this.activeEntries = ImmutableSet.copyOf(activeEntries);
		this.world = world;
		this.reporter = reporter;
	}

	@Override
	public C makeChild(String path) {
		this.reporter = reporter.forChild(path);
		return getThis();
	}

	@Override
	public C makeChild(String path, ReferenceKey key) {
		this.reporter = reporter.forChild(path, key);
		return getThis();
	}

	@Override
	public boolean markActive(ContextAware entry) {

		Set<ContextAware> newEntries = new ObjectOpenHashSet<>(this.activeEntries);
		boolean added = newEntries.add(entry);

		this.activeEntries = ImmutableSet.copyOf(newEntries);
		return added;

	}

	@Override
	public boolean markInActive(ContextAware entry) {

		Set<ContextAware> newEntries = new ObjectOpenHashSet<>(this.activeEntries);
		boolean removed = newEntries.remove(entry);

		this.activeEntries = ImmutableSet.copyOf(newEntries);
		return removed;

	}

	protected abstract C getThis();

}
