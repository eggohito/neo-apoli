package io.github.eggohito.neo_apoli.integration;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;

public class DependencyManager {

	public static final Event<Impl> ACTIONS = EventFactory.createArrayBacked(
		Impl.class,
		callbacks -> dependencies -> {

			for (var callback : callbacks) {
				callback.add(dependencies);
			}

		}
	);

	public static final Event<Impl> CONDITIONS = EventFactory.createArrayBacked(
		Impl.class,
		callbacks -> dependencies -> {

			for (var callback : callbacks) {
				callback.add(dependencies);
			}

		}
	);

	public static final Event<Impl> POWERS = EventFactory.createArrayBacked(
		Impl.class,
		callbacks -> dependencies -> {

			for (var callback : callbacks) {
				callback.add(dependencies);
			}

		}
	);

	public interface Impl {
		void add(ImmutableSet.Builder<ResourceLocation> dependencies);
	}

}
