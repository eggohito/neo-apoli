package io.github.eggohito.neo_apoli.api.event;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;

public class DependencyManager {

	public static final Event<Adder> ACTIONS = create();
	public static final Event<Adder> CONDITIONS = create();
	public static final Event<Adder> POWERS = create();
	public static final Event<Adder> GLOBAL_POWER_SETS = create();

	public interface Adder {
		void add(ImmutableSet.Builder<ResourceLocation> dependencies);
	}

	public static Event<Adder> create() {
		return EventFactory.createArrayBacked(
			Adder.class,
			callbacks -> dependencies -> {

				for (var callback : callbacks) {
					callback.add(dependencies);
				}

			}
		);
	}

}
