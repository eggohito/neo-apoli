package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNbtProviderTypes;
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

public enum BlockEntityNbtProvider implements NbtProvider {

	INSTANCE;

	public static final MapCodec<BlockEntityNbtProvider> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, BlockEntityNbtProvider> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public @NotNull NbtProvider.Type<?> getType() {
		return NeoApoliNbtProviderTypes.BLOCK_ENTITY;
	}

	@Override
	public @NotNull Tag nextTag(Context context) {

		HolderLookup.Provider wrapperLookup = context.level().registryAccess();
		Optional<BlockEntity> optBlockEntity = context.getOptional(NeoApoliContextParams.BLOCK_ENTITY);

		if (optBlockEntity.isEmpty()) {
			context.reportProblem("Couldn't get and provide NBT from non-existent block entity!");
		}

		return optBlockEntity
			.map(blockEntity -> blockEntity.saveWithFullMetadata(wrapperLookup))
			.orElseGet(CompoundTag::new);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.BLOCK_ENTITY);
	}

}
