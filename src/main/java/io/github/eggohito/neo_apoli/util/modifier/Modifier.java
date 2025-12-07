package io.github.eggohito.neo_apoli.util.modifier;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Modifier extends ContextAware, Comparable<Modifier> {

	Codec<Modifier> CODEC = ModifierType.CODEC.dispatch(Modifier::getType, ModifierType::mapCodec);

	StreamCodec<RegistryFriendlyByteBuf, Modifier> STREAM_CODEC = ModifierType.STREAM_CODEC.dispatch(Modifier::getType, ModifierType::packetCodec);

	@Override
	default int compareTo(@NotNull Modifier that) {

		if (this.phase() == that.phase()) {
			return Integer.compare(this.order(), that.order());
		}

		else {
			return this.phase().compareTo(that.phase());
		}

	}

	ModifierType<?> getType();

	Phase phase();

	int order();

	double apply(Context context, double base, double total);

	static <M extends Modifier> Products.P2<RecordCodecBuilder.Mu<M>, Phase, Integer> addPhaseAndOrderFields(RecordCodecBuilder.Instance<M> instance, int defaultOrder) {
		return instance.group(
			Phase.CODEC.fieldOf("phase").forGetter(Modifier::phase),
			Codec.INT.optionalFieldOf("order", defaultOrder).forGetter(Modifier::order)
		);
	}

	static double applyAll(List<Entry> entries, double baseValue) {

		if (entries.isEmpty()) {
			return baseValue;
		}

		List<Entry> sorted = new ObjectArrayList<>(entries);
		sorted.sort(Entry::compareTo);

		double currentBase = baseValue;
		double currentTotal = baseValue;

		Phase previousPhase = null;

		for (var entry : sorted) {

			Modifier modifier = entry.modifier();
			Context context = entry.context();

			Phase currentPhase = modifier.phase();

			if (currentPhase != previousPhase) {
				previousPhase = currentPhase;
				currentBase = currentTotal;
			}

			try {

				if (context.markActive(modifier)) {

					double value = modifier.apply(context, currentBase, currentTotal);

					if (!context.hasErrors()) {
						currentTotal = value;
					}

				}

			}

			finally {
				context.markInActive(modifier);
			}

		}

		return currentTotal;

	}

	static Entry entry(Modifier modifier, Context context) {
		return new Entry(modifier, context);
	}

	record Entry(Modifier modifier, Context context) implements Comparable<Entry> {

		@Override
		public int compareTo(@NotNull Modifier.Entry that) {
			return this.modifier().compareTo(that.modifier());
		}

	}

	enum Phase implements StringRepresentable {

		BASE {

			@Override
			public String getSerializedName() {
				return "base";
			}

		},

		TOTAL {

			@Override
			public String getSerializedName() {
				return "total";
			}

		};

		public static final Codec<Phase> CODEC = CodecUtil.enumType(Phase.class);

		public static final StreamCodec<ByteBuf, Phase> STREAM_CODEC = StreamCodecUtil.enumType(Phase.class);

	}

}
