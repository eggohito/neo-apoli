package io.github.eggohito.neo_apoli.modifier;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.modifier.custom.AddModifier;
import io.github.eggohito.neo_apoli.modifier.custom.MultiplyAdditiveModifier;
import io.github.eggohito.neo_apoli.modifier.custom.MultiplyMultiplicativeModifier;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Modifier extends ContextUser, Comparable<Modifier> {

	Codec<Modifier> CODEC = Type.CODEC.dispatch(Modifier::getType, Type::mapCodec);

	StreamCodec<RegistryFriendlyByteBuf, Modifier> STREAM_CODEC = Type.STREAM_CODEC.dispatch(Modifier::getType, Type::streamCodec);

	@Override
	default int compareTo(@NotNull Modifier that) {

		if (this.phase() == that.phase()) {
			return Integer.compare(this.order(), that.order());
		}

		else {
			return this.phase().compareTo(that.phase());
		}

	}

	Type<?> getType();

	Phase phase();

	int order();

	double apply(Context context, double base, double total);

	default Operation asOperation(Context context) {
		return new Operation(this, context);
	}

	static <M extends Modifier> Products.P2<RecordCodecBuilder.Mu<M>, Phase, Integer> addPhaseAndOrderFields(RecordCodecBuilder.Instance<M> instance, int defaultOrder) {
		return instance.group(
			Phase.CODEC.fieldOf("phase").forGetter(Modifier::phase),
			Codec.INT.optionalFieldOf("order", defaultOrder).forGetter(Modifier::order)
		);
	}

	static Modifier fromVanilla(AttributeModifier vanillaModifier) {

		AttributeModifier.Operation operation = vanillaModifier.operation();
		double amount = vanillaModifier.amount();

		return switch (operation) {
			case ADD_VALUE ->
				new AddModifier(Modifier.Phase.BASE, new ConstantNumberProvider(amount));
			case ADD_MULTIPLIED_BASE ->
				new MultiplyAdditiveModifier(Modifier.Phase.BASE, new ConstantNumberProvider(amount));
			case ADD_MULTIPLIED_TOTAL ->
				new MultiplyMultiplicativeModifier(Modifier.Phase.TOTAL, new ConstantNumberProvider(amount));
		};

	}

	static double applyAll(List<Operation> operations, double baseValue) {

		if (operations.isEmpty()) {
			return baseValue;
		}

		List<Operation> sorted = new ObjectArrayList<>(operations);
		sorted.sort(Operation::compareTo);

		double currentBase = baseValue;
		double currentTotal = baseValue;

		Phase previousPhase = null;
		for (var operation : sorted) {

			Modifier modifier = operation.modifier();
			Context context = operation.context();

			Phase currentPhase = modifier.phase();

			if (currentPhase != previousPhase) {
				previousPhase = currentPhase;
				currentBase = currentTotal;
			}

			try {

				if (context.visitor().push(modifier)) {

					double value = modifier.apply(context, currentBase, currentTotal);

					if (!context.hasErrors()) {
						currentTotal = value;
					}

				}

			}

			finally {
				context.visitor().pop(modifier);
			}

		}

		return currentTotal;

	}

	static Operation operation(Modifier modifier, Context context) {
		return new Operation(modifier, context);
	}

	record Operation(Modifier modifier, Context context) implements Comparable<Operation> {

		@Override
		public int compareTo(@NotNull Modifier.Operation that) {
			return this.modifier().compareTo(that.modifier());
		}

	}

	enum Phase {

		BASE,
		TOTAL;

		public static final Codec<Phase> CODEC = CodecUtil.enumType(Phase.class);
		public static final StreamCodec<ByteBuf, Phase> STREAM_CODEC = StreamCodecUtil.enumType(Phase.class);

	}

	record Type<M extends Modifier>(MapCodec<M> mapCodec, StreamCodec<RegistryFriendlyByteBuf, M> streamCodec) {

		public static final FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.MODIFIER_TYPE);

		public static final Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		public static final StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.MODIFIER_TYPE);

	}

}
