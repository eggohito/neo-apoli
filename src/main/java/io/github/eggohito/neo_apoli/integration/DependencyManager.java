package io.github.eggohito.neo_apoli.integration;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;

public class DependencyManager {

	public static final Event<Adder> ACTIONS = EventFactory.createArrayBacked(
		Adder.class,
		callbacks -> dependencies -> {

			for (var callback : callbacks) {
				callback.add(dependencies);
			}

		}
	);

	public static final Event<Adder> CONDITIONS = EventFactory.createArrayBacked(
		Adder.class,
		callbacks -> dependencies -> {

			for (var callback : callbacks) {
				callback.add(dependencies);
			}

		}
	);

	public static final Event<Adder> POWERS = EventFactory.createArrayBacked(
		Adder.class,
		callbacks -> dependencies -> {

			for (var callback : callbacks) {
				callback.add(dependencies);
			}

		}
	);

	public interface Adder {
		void add(ImmutableSet.Builder<ResourceLocation> dependencies);
	}

}
