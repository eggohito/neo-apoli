package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.block.Block;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public record IsInTagBlockCondition(TagKey<Block> tag) implements BlockCondition {

	public static final MapCodec<IsInTagBlockCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TagKey.codec(RegistryKeys.BLOCK).fieldOf("tag").forGetter(IsInTagBlockCondition::tag)
	).apply(instance, IsInTagBlockCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsInTagBlockCondition> PACKET_CODEC = PacketCodec.tuple(
		TagKey.packetCodec(RegistryKeys.BLOCK), IsInTagBlockCondition::tag,
		IsInTagBlockCondition::new
	);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.IS_IN_TAG;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(ContextParameters.BLOCK_STATE)
			.map(state -> state.isIn(this.tag()))
			.orElse(false);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		BlockCondition.super.validate(reporter);
		RegistryUtil.validateTag(reporter.makeChild(".tag"), this.tag());
	}

}
