package io.github.eggohito.neo_apoli.event;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.util.PowerReference;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.Nullable;

public final class PowerParsingEvents {

	public static final Event<Decoding> DECODING = EventFactory.createArrayBacked(
		Decoding.class,
		callbacks -> new Decoding() {

			@Override
			public <I> DataResult<Power> decode(PowerReference reference, PowerType<?> type, DynamicOps<I> ops, MapLike<I> mapInput) {

				DataResult<Power> result = null;
				for (var callback : callbacks) {

					result = callback.decode(reference, type, ops, mapInput);

					if (result != null) {
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
		@Nullable
		<I> DataResult<Power> decode(PowerReference reference, PowerType<?> type, DynamicOps<I> ops, MapLike<I> mapInput);
	}

	public interface Encoding {
		<I> void encode(PowerReference reference, Power power, DynamicOps<I> ops, RecordBuilder<I> prefix);
	}

}
