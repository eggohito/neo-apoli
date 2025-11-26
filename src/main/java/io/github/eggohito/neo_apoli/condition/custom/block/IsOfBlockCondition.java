package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;

public record IsOfBlockCondition(Holder<Block> block) implements BlockCondition {

	public static final MapCodec<IsOfBlockCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("block").forGetter(IsOfBlockCondition::block)
	).apply(instance, IsOfBlockCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsOfBlockCondition> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.holderRegistry(Registries.BLOCK), IsOfBlockCondition::block,
		IsOfBlockCondition::new
	);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.IS_OF;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextKeys.BLOCK_STATE)
			.map(state -> state.is(this.block()))
			.orElse(false);
	}

}
