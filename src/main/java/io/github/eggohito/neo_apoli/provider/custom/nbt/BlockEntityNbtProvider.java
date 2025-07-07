package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.NbtProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.context.ContextParameter;

import java.util.Optional;
import java.util.Set;

@EqualsAndHashCode
@Data
public final class BlockEntityNbtProvider extends NbtProvider {

	public static final MapCodec<BlockEntityNbtProvider> CODEC = MapCodec.unit(BlockEntityNbtProvider::new);
	public static final PacketCodec<RegistryByteBuf, BlockEntityNbtProvider> PACKET_CODEC = PacketCodec.unit(new BlockEntityNbtProvider());

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.BLOCK_ENTITY;
	}

	@Override
	protected NbtElement impl(Context context) {

		RegistryWrapper.WrapperLookup wrapperLookup = context.getWorld().getRegistryManager();
		Optional<BlockEntity> blockEntity = context.optional(ContextParameters.BLOCK_ENTITY);

		if (blockEntity.isEmpty()) {
			context.getReporter().report("Couldn't get and provide NBT from non-existent block entity!");
		}

		return blockEntity
			.map(be -> be.createNbtWithIdentifyingData(wrapperLookup))
			.orElseGet(NbtCompound::new);

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.BLOCK_ENTITY);
	}

}
