package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public record BlockEntityNbtProvider() implements NbtProvider {

	public static final MapCodec<BlockEntityNbtProvider> CODEC = MapCodec.unit(BlockEntityNbtProvider::new);
	public static final PacketCodec<RegistryByteBuf, BlockEntityNbtProvider> PACKET_CODEC = PacketCodecUtil.unit(BlockEntityNbtProvider::new);

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.BLOCK_ENTITY;
	}

	@Override
	public @NotNull NbtElement next(Context context) {

		RegistryWrapper.WrapperLookup wrapperLookup = context.getWorld().getRegistryManager();
		Optional<BlockEntity> optBlockEntity = context.optional(ContextParameters.BLOCK_ENTITY);

		if (optBlockEntity.isEmpty()) {
			context.getReporter().report("Couldn't get and provide NBT from non-existent block entity!");
		}

		return optBlockEntity
			.map(blockEntity -> blockEntity.createNbtWithIdentifyingData(wrapperLookup))
			.orElseGet(NbtCompound::new);

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.BLOCK_ENTITY);
	}

}
