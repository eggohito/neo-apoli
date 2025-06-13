package io.github.eggohito.neo_apoli.event;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.util.PowerReference;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class PowerParsingEvents {

	public static final Event<Decoding> DECODING = EventFactory.createArrayBacked(
		Decoding.class,
		callbacks -> new Decoding() {

			@Override
			public <I> DataResult<Unit> decode(PowerReference reference, Power.Serializer<?> serializer, DynamicOps<I> ops, MapLike<I> mapInput) {

				DataResult<Unit> result = DataResult.success(Unit.INSTANCE, Lifecycle.stable());
				for (var callback : callbacks) {

					result = result.apply2stable((u, o) -> u, callback.decode(reference, serializer, ops, mapInput));

					if (result.isError()) {
						return result;
					}

				}

				return result;

			}

		}
	);

	public static final Event<Encoding> ENCODING = EventFactory.createArrayBacked(
		Encoding.class,
		callbacks -> new Encoding() {

			@Override
			public <I> void encode(PowerReference reference, Power power, DynamicOps<I> ops, RecordBuilder<I> prefix) {

				for (var callback : callbacks) {
					callback.encode(reference, power, ops, prefix);
				}

			}

		}
	);

	public interface Decoding {
		<I> DataResult<Unit> decode(PowerReference reference, Power.Serializer<?> serializer, DynamicOps<I> ops, MapLike<I> mapInput);
	}

	public interface Encoding {
		<I> void encode(PowerReference reference, Power power, DynamicOps<I> ops, RecordBuilder<I> prefix);
	}

}
