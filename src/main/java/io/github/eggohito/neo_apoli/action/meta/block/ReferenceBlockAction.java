package io.github.eggohito.neo_apoli.action.meta.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceBlockAction(Identifier value) implements BlockAction, ReferenceMetaAction<BlockAction, BlockActionType<?>> {

	public static final MapCodec<ReferenceBlockAction> CODEC = ReferenceMetaAction.codec(ReferenceBlockAction::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceBlockAction> PACKET_CODEC = ReferenceMetaAction.packetCodec(ReferenceBlockAction::new);

	@Override
	public ActionCategory<BlockAction> getCategory() {
		return BlockAction.super.getCategory();
	}

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.REFERENCE;
	}

}
