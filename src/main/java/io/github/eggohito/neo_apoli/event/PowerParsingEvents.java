package io.github.eggohito.neo_apoli.event;

import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.util.PowerEntry;
import io.github.eggohito.neo_apoli.util.PowerReference;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.function.Function;

public final class PowerParsingEvents {

	public static final Event<Decoding> DECODING = EventFactory.createArrayBacked(
		Decoding.class,
		callbacks -> new Decoding() {

			@Override
			public <I> DataResult<Power> decode(PowerReference reference, PowerType<?> type, DynamicOps<I> ops, MapLike<I> mapInput) {

				for (var callback : callbacks) {

					DataResult<Power> result = callback.decode(reference, type, ops, mapInput);

					if (result != null) {
						return result;
					}

				}

				MapCodec<? extends Power> mapCodec = type.mapCodec();
				return mapCodec.decode(ops, mapInput).map(Function.identity());

			}

		}
	);

	public static final Event<Encoding> ENCODING = EventFactory.createArrayBacked(
		Encoding.class,
		callbacks -> new Encoding() {

			@Override
			public <I> void encode(PowerEntry<?> entry, DynamicOps<I> ops, RecordBuilder<I> prefix) {

				for (var callback : callbacks) {
					callback.encode(entry, ops, prefix);
				}

			}

		}
	);

	public interface Decoding {
		<I> DataResult<Power> decode(PowerReference reference, PowerType<?> type, DynamicOps<I> ops, MapLike<I> mapInput);
	}

	public interface Encoding {
		<I> void encode(PowerEntry<?> entry, DynamicOps<I> ops, RecordBuilder<I> prefix);
	}

}
