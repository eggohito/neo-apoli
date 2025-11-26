package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.LoopMetaAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record LoopItemAction(Optional<ItemAction> beforeAction, Optional<ItemAction> afterAction, NumberProvider iterations, ItemAction action) implements ItemAction, LoopMetaAction<ItemAction> {

	public static final MapCodec<LoopItemAction> CODEC = MapCodecUtil.lazy(LoopItemAction.class.getSimpleName(), () -> LoopMetaAction.createCodec(ItemAction.CODEC, LoopItemAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, LoopItemAction> STREAM_CODEC = StreamCodecUtil.lazy(LoopItemAction.class.getSimpleName(), () -> LoopMetaAction.createStreamCodec(ItemAction.STREAM_CODEC, LoopItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.LOOP;
	}

	@Override
	public void execute(Context context) {
		LoopMetaAction.super.execute(context);
	}

	@Override
	public void serverExecute(ServerContext context) {
		this.execute(context);
	}

	@Override
	public String asDisplayString() {
		return ItemAction.super.asDisplayString();
	}

}
