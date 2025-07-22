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
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class IsInTagBlockCondition extends BlockCondition {

	public static final MapCodec<IsInTagBlockCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TagKey.unprefixedCodec(RegistryKeys.BLOCK).fieldOf("tag").forGetter(IsInTagBlockCondition::tag)
	).apply(instance, IsInTagBlockCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsInTagBlockCondition> PACKET_CODEC = PacketCodec.tuple(
		TagKey.packetCodec(RegistryKeys.BLOCK), IsInTagBlockCondition::tag,
		IsInTagBlockCondition::new
	);

	private final TagKey<Block> tag;

	public IsInTagBlockCondition(TagKey<Block> tag) {
		this.tag = tag;
	}

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.IS_IN_TAG;
	}

	@Override
	protected boolean impl(Context context) {
		return context.required(ContextParameters.BLOCK_STATE).isIn(this.tag());
	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);
		Optional<RegistryEntryLookup<Block>> blockRegistry = reporter.getWrapperLookup().flatMap(wrapperLookup -> wrapperLookup.getOptional(this.tag().registryRef()));

		blockRegistry.ifPresent(lookup -> lookup.getOptional(this.tag()).ifPresentOrElse(entries -> {}, () -> reporter.makeChild(".tag").report("Block tag \"" + this.tag().id() + "\" does not exist!")));

	}

}
