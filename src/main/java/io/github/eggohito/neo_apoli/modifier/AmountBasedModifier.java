package io.github.eggohito.neo_apoli.modifier;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public interface AmountBasedModifier extends Modifier {

	@Override
	Type<?> getType();

	@Override
	default List<Operation> collectNestedOps(Operation parent) {

		List<Operation> nestedOps = new ObjectArrayList<>();
		MiscUtil.iterateList(
			this.modifiers(),
			(index, nestedMod) -> nestedOps.add(nestedMod.asOperation(parent.context().forChild(".modifiers[" + index + "]")))
		);

		return nestedOps;

	}

	@Override
	default double apply(Context context, double base, double total) {
		return calculate(amount().getFloat(context.forChild(".amount")), base, total);
	}

	@Override
	default void validate(Context.Validator validator) {
		Modifier.super.validate(validator);
		amount().validate(validator.forChild(".amount"));
	}

	FloatProvider amount();

	double calculate(double amount, double base, double total);

	static <M extends AmountBasedModifier> MapCodec<M> mapCodec(Function3<Phase, FloatProvider, List<Modifier>, M> constructor) {
		return MapCodecUtil.lazy(AmountBasedModifier.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> Modifier
			.addPhaseField(instance)
			.and(FloatProvider.CODEC.fieldOf("amount").forGetter(AmountBasedModifier::amount))
			.and(Modifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(Modifier::modifiers))
			.apply(instance, constructor)
		));
	}

	static <M extends AmountBasedModifier> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(Function3<Phase, FloatProvider, List<Modifier>, M> constructor) {
		return StreamCodecUtil.lazy(AmountBasedModifier.class.getSimpleName(), () -> StreamCodec.composite(
			Phase.STREAM_CODEC, AmountBasedModifier::phase,
			FloatProvider.STREAM_CODEC, AmountBasedModifier::amount,
			Modifier.STREAM_CODEC.apply(ByteBufCodecs.list()), Modifier::modifiers,
			constructor
		));
	}

}
