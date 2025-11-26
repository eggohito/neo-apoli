package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.meta.RandomChanceMetaAction;
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

public record RandomChanceItemAction(ItemAction successAction, Optional<ItemAction> failAction, NumberProvider chance) implements ItemAction, RandomChanceMetaAction<ItemAction> {

	public static final MapCodec<RandomChanceItemAction> CODEC = MapCodecUtil.lazy(RandomChanceItemAction.class.getSimpleName(), () -> RandomChanceMetaAction.createCodec(ItemAction.CODEC, RandomChanceItemAction::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, RandomChanceItemAction> STREAM_CODEC = StreamCodecUtil.lazy(RandomChanceItemAction.class.getSimpleName(), () -> RandomChanceMetaAction.createStreamCodec(ItemAction.STREAM_CODEC, RandomChanceItemAction::new));

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.RANDOM_CHANCE;
	}

	@Override
	public void execute(Context context) {
		RandomChanceMetaAction.super.execute(context);
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
