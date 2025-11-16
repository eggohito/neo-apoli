package io.github.eggohito.neo_apoli.util.modifier;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierType;
import io.github.eggohito.neo_apoli.util.modifier.type.ModifierTypes;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.function.IntFunction;

public interface Modifier extends ContextAware, Comparable<Modifier> {

	Codec<Modifier> CODEC = ModifierTypes.CODEC.dispatch(Modifier::getType, ModifierType::mapCodec);

	PacketCodec<RegistryByteBuf, Modifier> PACKET_CODEC = ModifierTypes.PACKET_CODEC.dispatch(Modifier::getType, ModifierType::packetCodec);

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

	static double applyAll(IntFunction<Context> mapper, Collection<Modifier> modifiers, double baseValue) {

		if (modifiers.isEmpty()) {
			return baseValue;
		}

		else {

			List<Modifier> sortedModifiers = new ObjectArrayList<>(modifiers);
			sortedModifiers.sort(Modifier::compareTo);

			double currentBase = baseValue;
			double currentTotal = baseValue;

			Phase previousPhase = null;
			ListIterator<Modifier> listIterator = sortedModifiers.listIterator();

			while (listIterator.hasNext()) {

				int index = listIterator.nextIndex();
				Modifier modifier = listIterator.next();

				Phase currentPhase = modifier.phase();

				if (previousPhase == null) {
					previousPhase = currentPhase;
				}

				if (previousPhase != currentPhase) {
					previousPhase = currentPhase;
					currentBase = currentTotal;
				}

				Context modifierContext = mapper.apply(index);
				double value = modifier.apply(modifierContext, currentBase, currentTotal);

				if (!modifierContext.hasErrors()) {
					currentTotal = value;
				}

			}

			return currentTotal;

		}

	}

	enum Phase implements StringIdentifiable {

		BASE {

			@Override
			public String asString() {
				return "base";
			}

		},

		TOTAL {

			@Override
			public String asString() {
				return "total";
			}

		};

		public static final Codec<Phase> CODEC = CodecUtil.enumType(Phase.class);

		public static final PacketCodec<ByteBuf, Phase> PACKET_CODEC = PacketCodecUtil.enumType(Phase.class);

	}

}
