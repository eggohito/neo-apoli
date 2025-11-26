package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface ItemAction extends Action {

	Codec<ItemAction> CODEC = Codec.recursive(ItemAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(ItemActionType.CODEC.dispatch(ItemAction::getType, ItemActionType::mapCodec), codec.listOf().xmap(SequenceItemAction::new, SequenceItemAction::actions), NothingItemAction.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, ItemAction> STREAM_CODEC = ItemActionType.STREAM_CODEC.dispatch(ItemAction::getType, ItemActionType::streamCodec);

	@Override
	ItemActionType<?> getType();

	@Override
	default void execute(Context context) {

		if (context.getWorld() instanceof ServerLevel serverWorld) {
			this.serverExecute(new ServerContext.Builder(context).build(serverWorld));
		}

	}

	void serverExecute(ServerContext context);

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.STACK_REFERENCE);
	}

	@Override
	default String asDisplayString() {
		return "Item action with type \"" + RegistryUtil.getId(NeoApoliRegistries.ACTION_TYPE, this.getType()) + "\"";
	}

}
