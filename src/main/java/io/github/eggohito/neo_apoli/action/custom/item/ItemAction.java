package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public interface ItemAction extends Action {

	Codec<ItemAction> CODEC = Codec.recursive(ItemAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(ItemActionType.CODEC.dispatch(ItemAction::getType, ItemActionType::mapCodec), codec.listOf().xmap(SequenceItemAction::new, SequenceItemAction::actions), NothingItemAction.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, ItemAction> PACKET_CODEC = ItemActionType.PACKET_CODEC.dispatch(ItemAction::getType, ItemActionType::packetCodec);

	@Override
	ItemActionType<?> getType();

	@Override
	default void execute(Context context) {

		if (context.getWorld() instanceof ServerWorld serverWorld) {
			this.serverExecute(new ServerContext.Builder(context).build(serverWorld));
		}

	}

	void serverExecute(ServerContext context);

	@Override
	default Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.STACK_REFERENCE);
	}

	@Override
	default String asDisplayString() {
		return "Item action with type \"" + RegistryUtil.getId(NeoApoliRegistries.ACTION_TYPE, this.getType()) + "\"";
	}

}
