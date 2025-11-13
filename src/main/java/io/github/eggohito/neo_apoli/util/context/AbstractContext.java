package io.github.eggohito.neo_apoli.util.context;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.world.World;

import java.util.Set;

@Getter
public abstract class AbstractContext<C extends Context> implements Context {

	private final ContextParameterMap parameters;
	private final World world;

	private ImmutableSet<ContextAware> activeEntries;
	private ContextAware.ErrorReporter reporter;

	protected AbstractContext(ContextParameterMap.Builder parameters, Set<ContextAware> activeEntries, World world, ContextAware.ErrorReporter reporter) {
		this.parameters = parameters.build(reporter.getContextType());
		this.activeEntries = ImmutableSet.copyOf(activeEntries);
		this.world = world;
		this.reporter = reporter;
	}

	@Override
	public C makeChild(String path) {
		this.reporter = reporter.makeChild(path);
		return getThis();
	}

	@Override
	public C makeChild(String path, ContextKey key) {
		this.reporter = reporter.makeChild(path, key);
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
