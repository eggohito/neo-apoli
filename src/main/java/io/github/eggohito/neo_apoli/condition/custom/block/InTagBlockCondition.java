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
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public record InTagBlockCondition(TagKey<Block> tag) implements BlockCondition {

	public static final MapCodec<InTagBlockCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TagKey.unprefixedCodec(RegistryKeys.BLOCK).fieldOf("tag").forGetter(InTagBlockCondition::tag)
	).apply(instance, InTagBlockCondition::new));

	public static final PacketCodec<RegistryByteBuf, InTagBlockCondition> PACKET_CODEC = PacketCodec.tuple(
		TagKey.packetCodec(RegistryKeys.BLOCK), InTagBlockCondition::tag,
		InTagBlockCondition::new
	);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.IN_TAG;
	}

	@Override
	public boolean test(Context context) {
		BlockPos blockPos = BlockPos.ofFloored(context.requiredParameter(ContextParameters.POSITION));
		return context.optionalParameter(ContextParameters.BLOCK_STATE)
			.orElseGet(() -> context.getWorld().getBlockState(blockPos))
			.isIn(this.tag());
	}

	@Override
	public void validate(ErrorReporter reporter) {

		BlockCondition.super.validate(reporter);
		Optional<RegistryWrapper.Impl<Block>> blockRegistry = reporter.getWrapperLookup().flatMap(wrapperLookup -> wrapperLookup.getOptional(RegistryKeys.BLOCK));

		blockRegistry.ifPresent(blockImpl -> blockImpl.getOptional(this.tag()).ifPresentOrElse(entries -> {}, () -> reporter.report("Block tag \"" + this.tag().id() + "\" does not exist!")));

	}

}
