package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.block.Block;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record IsOfBlockCondition(RegistryEntry<Block> block) implements BlockCondition {

	public static final MapCodec<IsOfBlockCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Registries.BLOCK.getEntryCodec().fieldOf("block").forGetter(IsOfBlockCondition::block)
	).apply(instance, IsOfBlockCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsOfBlockCondition> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryEntry(RegistryKeys.BLOCK), IsOfBlockCondition::block,
		IsOfBlockCondition::new
	);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.IS_OF;
	}

	@Override
	public boolean test(Context context) {

		World world = context.getWorld();
		BlockPos pos = BlockPos.ofFloored(context.requiredParameter(ContextParameters.POSITION));

		return context.optionalParameter(ContextParameters.BLOCK_STATE)
			.orElseGet(() -> world.getBlockState(pos))
			.isOf(block());

	}

}
