package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public record BlockEntityNbtProvider() implements NbtProvider {

	public static final MapCodec<BlockEntityNbtProvider> CODEC = MapCodec.unit(BlockEntityNbtProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, BlockEntityNbtProvider> STREAM_CODEC = StreamCodecUtil.unit(BlockEntityNbtProvider::new);

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.BLOCK_ENTITY;
	}

	@Override
	public @NotNull Tag next(Context context) {

		HolderLookup.Provider wrapperLookup = context.getLevel().registryAccess();
		Optional<BlockEntity> optBlockEntity = context.optional(NeoApoliContextKeys.BLOCK_ENTITY);

		if (optBlockEntity.isEmpty()) {
			context.getValidator().report("Couldn't get and provide NBT from non-existent block entity!");
		}

		return optBlockEntity
			.map(blockEntity -> blockEntity.saveWithFullMetadata(wrapperLookup))
			.orElseGet(CompoundTag::new);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.BLOCK_ENTITY);
	}

}
