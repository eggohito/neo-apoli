package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record ReferenceBlockAction(Identifier value) implements BlockAction, ReferenceMetaAction<BlockAction> {

	public static final MapCodec<ReferenceBlockAction> CODEC = ReferenceMetaAction.codec(ReferenceBlockAction::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceBlockAction> PACKET_CODEC = ReferenceMetaAction.packetCodec(ReferenceBlockAction::new);

	@Override
	public Pair<Class<BlockAction>, String> classAndName() {
		return Pair.of(BlockAction.class, "Block action");
	}

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.REFERENCE;
	}

	@Override
	public void execute(Context context) {
		ReferenceMetaAction.super.execute(context);
	}

	@Override
	public void serverExecute(ServerContext context) {
		ReferenceMetaAction.super.execute(context);
	}

	@Override
	public String asDisplayString() {
		return BlockAction.super.asDisplayString();
	}

}
