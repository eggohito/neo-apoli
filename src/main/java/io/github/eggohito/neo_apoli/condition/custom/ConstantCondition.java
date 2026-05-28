package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ConstantCondition(boolean value) implements Condition {

	public static final MapCodec<ConstantCondition> CODEC = Codec.BOOL.fieldOf("value").xmap(
		ConstantCondition::new,
		ConstantCondition::value
	);

	public static final Codec<ConstantCondition> INLINE_CODEC = Codec.BOOL.xmap(
		ConstantCondition::new,
		ConstantCondition::value
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantCondition> STREAM_CODEC = ByteBufCodecs.BOOL.map(
		ConstantCondition::new,
		ConstantCondition::value
	).cast();

	@Override
	public Type<?> getType() {
		return NeoApoliConditionTypes.CONSTANT;
	}

	@Override
	public boolean test(Context context) {
		return value();
	}

}
