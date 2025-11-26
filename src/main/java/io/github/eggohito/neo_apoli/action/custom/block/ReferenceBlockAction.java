package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.ReferenceMetaAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceBlockAction(ResourceLocation value) implements BlockAction, ReferenceMetaAction<BlockAction> {

	public static final MapCodec<ReferenceBlockAction> CODEC = ReferenceMetaAction.createCodec(ReferenceBlockAction::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceBlockAction> STREAM_CODEC = ReferenceMetaAction.createStreamCodec(ReferenceBlockAction::new);

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
