package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.category.ActionCategories;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.meta.item.SequenceItemAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
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

public abstract class ItemAction extends Action {

	public static final MapCodec<ItemAction> MAP_CODEC = ItemActionTypes.CODEC.dispatchMap("type", ItemAction::getType, ItemActionType::mapCodec);
	public static final Codec<ItemAction> BASE_CODEC = MAP_CODEC.codec();

	public static final Codec<ItemAction> CODEC = Codec.recursive(ItemAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(BASE_CODEC, codec.listOf().xmap(SequenceItemAction::new, SequenceItemAction::actions)));
	public static final PacketCodec<RegistryByteBuf, ItemAction> PACKET_CODEC = ItemActionTypes.PACKET_CODEC.dispatch(ItemAction::getType, ItemActionType::packetCodec);

	@Override
	public abstract ItemActionType<?> getType();

	@Override
	public ActionCategory<ItemAction> getCategory() {
		return ActionCategories.ITEM_ACTION;
	}

	protected final void impl(Context context) {

		if (context.getWorld() instanceof ServerWorld serverWorld) {
			this.impl(new ServerContext(context, serverWorld));
		}

	}

	protected abstract void impl(ServerContext context);

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.STACK_REFERENCE);
	}

	@Override
	public String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.ITEM_ACTION_TYPE, this.getType()) + "\"";
	}

}
