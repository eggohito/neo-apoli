package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.category.ActionCategories;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.meta.entity.SequenceEntityAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public abstract class EntityAction extends Action {

	public static final MapCodec<EntityAction> MAP_CODEC = EntityActionType.CODEC.dispatchMap("type", EntityAction::getType, EntityActionType::mapCodec);
	public static final Codec<EntityAction> BASE_CODEC = MAP_CODEC.codec();

	public static final Codec<EntityAction> CODEC = Codec.recursive(EntityAction.class.getSimpleName(), codec -> new MultiAlternativeCodec<>(BASE_CODEC, codec.listOf().xmap(SequenceEntityAction::new, SequenceEntityAction::actions)));
	public static final PacketCodec<RegistryByteBuf, EntityAction> PACKET_CODEC = EntityActionType.PACKET_CODEC.dispatch(EntityAction::getType, EntityActionType::packetCodec);

	@Override
	public abstract EntityActionType<?> getType();

	@Override
	public void execute(Context context) {

		context = new ContextImpl.Builder(context)
			.add(ContextParameters.POSITION, context.required(ContextParameters.ENTITY_POS))
			.build(context.getWorld());

		super.execute(context);

	}

	@Override
	public ActionCategory<EntityAction> getCategory() {
		return ActionCategories.ENTITY_ACTION;
	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return ContextTypes.ENTITY.getAllowed();
	}

	@Override
	public String asDisplayString() {
		return this.getCategory() + " with type \"" + RegistryUtil.getId(NeoApoliRegistries.ENTITY_ACTION_TYPE, this.getType()) + "\"";
	}

}
