package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.block.Block;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

@EqualsAndHashCode
@Data
public final class IsOfBlockCondition extends BlockCondition {

	public static final MapCodec<IsOfBlockCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Registries.BLOCK.getEntryCodec().fieldOf("block").forGetter(IsOfBlockCondition::block)
	).apply(instance, IsOfBlockCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsOfBlockCondition> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryEntry(RegistryKeys.BLOCK), IsOfBlockCondition::block,
		IsOfBlockCondition::new
	);

	private final RegistryEntry<Block> block;

	public IsOfBlockCondition(RegistryEntry<Block> block) {
		this.block = block;
	}

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.IS_OF;
	}

	@Override
	protected boolean impl(Context context) {
		return context.required(ContextParameters.BLOCK_STATE).isOf(block());
	}

}
