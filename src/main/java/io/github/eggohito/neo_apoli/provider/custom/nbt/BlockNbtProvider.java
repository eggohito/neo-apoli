package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.block.BlockProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNbtProviderTypes;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record BlockNbtProvider(BlockProvider block) implements NbtProvider {

	public static final MapCodec<BlockNbtProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(BlockProvider.CODEC.fieldOf("block").forGetter(BlockNbtProvider::block))
		.apply(instance, BlockNbtProvider::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, BlockNbtProvider> STREAM_CODEC = StreamCodec.composite(
		BlockProvider.STREAM_CODEC, BlockNbtProvider::block,
		BlockNbtProvider::new
	);

	@Override
	public @NotNull NbtProvider.Type<?> getType() {
		return NeoApoliNbtProviderTypes.BLOCK;
	}

	@Override
	public @NotNull Tag getTag(Context context) {

		RegistryAccess registryAccess = context.level().registryAccess();
		Optional<CachedBlock> block = block().getBlock(context.forChild(".block"));

		if (block.isEmpty()) {
			context.forChild(".block").reportProblem("Block doesn't exist!");
		}

		return block
			.map(self -> this.serialize(registryAccess, self))
			.orElseGet(CompoundTag::new);

	}

	@Override
	public void validate(Context.Validator validator) {
		NbtProvider.super.validate(validator);
		block().validate(validator.forChild(".block"));
	}

	private CompoundTag serialize(RegistryAccess registryAccess, CachedBlock block) {

		RegistryOps<Tag> ops = registryAccess.createSerializationContext(NbtOps.INSTANCE);
		CompoundTag compoundTag = new CompoundTag();

		Tag stateTag = BlockState.CODEC.encodeStart(ops, block.state()).result().orElse(null);
		CompoundTag entityTag = Optional.ofNullable(block.entity()).map(self -> self.saveWithFullMetadata(registryAccess)).orElse(null);

		if (stateTag != null) {
			compoundTag.put("state", stateTag);
		}

		if (entityTag != null) {

			if (!compoundTag.contains("state")) {
				compoundTag = entityTag;
			}

			else {
				compoundTag.put("entity", entityTag);
			}

		}

		return compoundTag;

	}

}
