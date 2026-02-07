package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.IReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceBlockAction(ResourceLocation value) implements BlockAction, IReferenceMetaAction<BlockAction> {

	public static final MapCodec<ReferenceBlockAction> MAP_CODEC = IReferenceMetaAction.mapCodec(ReferenceBlockAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceBlockAction> STREAM_CODEC = IReferenceMetaAction.streamCodec(ReferenceBlockAction::new);

	@Override
	public Pair<Class<BlockAction>, String> classAndName() {
		return Pair.of(BlockAction.class, "Block action");
	}

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.REFERENCE;
	}

}
