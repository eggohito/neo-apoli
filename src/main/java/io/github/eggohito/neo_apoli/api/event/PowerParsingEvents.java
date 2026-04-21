package io.github.eggohito.neo_apoli.api.event;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class PowerParsingEvents {

	public static final Event<Decoding> DECODING = EventFactory.createArrayBacked(
		Decoding.class,
		callbacks -> new Decoding() {

			@Override
			public <I> void decode(PowerHolder<?> power, DynamicOps<I> ops, MapLike<I> mapInput) {

				for (var callback : callbacks) {
					callback.decode(power, ops, mapInput);
				}

			}

		}
	);

	public static final Event<Encoding> ENCODING = EventFactory.createArrayBacked(
		Encoding.class,
		callbacks -> new Encoding() {

			@Override
			public <I> void encode(PowerHolder<?> power, DynamicOps<I> ops, RecordBuilder<I> prefix) {

				for (var callback : callbacks) {
					callback.encode(power, ops, prefix);
				}

			}

		}
	);

	public interface Decoding {
		<I> void decode(PowerHolder<?> power, DynamicOps<I> ops, MapLike<I> mapInput);
	}

	public interface Encoding {
		<I> void encode(PowerHolder<?> power, DynamicOps<I> ops, RecordBuilder<I> prefix);
	}

}
