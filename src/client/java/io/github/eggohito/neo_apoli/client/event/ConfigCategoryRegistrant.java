package io.github.eggohito.neo_apoli.client.event;

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
			public void save() {

				for (var callback : callbacks) {
					callback.save();
				}

			}

		}
	);

	interface Entry {

		void addGroup(Consumer<OptionGroup> adder);

		void save();

	}

}
