package io.github.eggohito.neo_apoli.util.modifier;

import com.mojang.serialization.Codec;
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

public interface Modifier extends ContextAware, Comparable<Modifier> {

	Codec<Modifier> CODEC = ModifierTypes.CODEC.dispatch("type", Modifier::getType, ModifierType::mapCodec);
	PacketCodec<RegistryByteBuf, Modifier> PACKET_CODEC = ModifierTypes.PACKET_CODEC.dispatch(Modifier::getType, ModifierType::packetCodec);

	@Override
	default int compareTo(@NotNull Modifier that) {

		if (this.getPhase() == that.getPhase()) {
			return Integer.compare(this.getOrder(), that.getOrder());
		}

		else {
			return this.getPhase().compareTo(that.getPhase());
		}

	}

	ModifierType<?> getType();

	Phase getPhase();

	int getOrder();

	double apply(Context context, double base, double total);

	static double applyAll(Context context, Collection<Modifier> modifiers, double baseValue) {

		if (modifiers.isEmpty()) {
			return baseValue;
		}

		else {

			List<Modifier> sortedModifiers = new ObjectArrayList<>(modifiers);
			sortedModifiers.sort(Modifier::compareTo);

			double currentBase = baseValue;
			double currentTotal = baseValue;

			Phase prevPhase = Phase.BASE;
			ListIterator<Modifier> sortedModifierIterator = sortedModifiers.listIterator();

			while (sortedModifierIterator.hasNext()) {

				int index = sortedModifierIterator.nextIndex();
				Modifier sortedModifier = sortedModifierIterator.next();

				Phase currPhase = sortedModifier.getPhase();
				if (prevPhase != currPhase) {
					prevPhase = currPhase;
					currentBase = currentTotal;
				}

				Context modifierContext = context.makeChild("." + context.getReporter().getPath() + "[" + index + "]");
				double tempTotal = sortedModifier.apply(modifierContext, currentBase, currentTotal);

				if (!modifierContext.hasErrors()) {
					currentTotal = tempTotal;
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
