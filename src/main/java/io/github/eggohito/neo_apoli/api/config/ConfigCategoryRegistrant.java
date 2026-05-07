package io.github.eggohito.neo_apoli.api.config;

import dev.isxander.yacl3.api.OptionGroup;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.function.Consumer;

public interface ConfigCategoryRegistrant {

	Event<Entry> HUD_ELEMENT_TYPE = EventFactory.createArrayBacked(
		Entry.class,
		callbacks -> new Entry() {

			@Override
			public void addGroup(Consumer<OptionGroup> adder) {

				for (var callback : callbacks) {
					callback.addGroup(adder);
				}

			}

			@Override
			public boolean load() {

				for (var callback : callbacks) {

					if (!callback.load()) {
						return false;
					}

				}

				return true;

			}

			@Override
			public void save() {

				for (var callback : callbacks) {
					callback.save();
				}

			}

		}
	);

	Event<Entry> POWER_TYPE = EventFactory.createArrayBacked(
		Entry.class,
		callbacks -> new Entry() {

			@Override
			public void addGroup(Consumer<OptionGroup> adder) {

				for (var callback : callbacks) {
					callback.addGroup(adder);
				}

			}

			@Override
			public boolean load() {

				for (var callback : callbacks) {

					if (!callback.load()) {
						return false;
					}

				}

				return true;

			}

			@Override
			public void save() {

				for (var callback : callbacks) {
					callback.save();
				}

			}

		}
	);

	interface Entry {

		void addGroup(Consumer<OptionGroup> adder);

		boolean load();

		void save();

	}

}
