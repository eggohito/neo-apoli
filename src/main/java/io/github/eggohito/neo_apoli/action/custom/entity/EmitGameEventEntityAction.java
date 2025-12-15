package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record EmitGameEventEntityAction(Holder<GameEvent> gameEvent) implements EntityAction {

	public static final MapCodec<EmitGameEventEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(GameEvent.CODEC.fieldOf("game_event").forGetter(EmitGameEventEntityAction::gameEvent))
		.apply(instance, EmitGameEventEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EmitGameEventEntityAction> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.holderRegistry(Registries.GAME_EVENT), EmitGameEventEntityAction::gameEvent,
		EmitGameEventEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EMIT_GAME_EVENT;
	}

	@Override
	public void execute(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		Entity entity = context.required(NeoApoliContextKeys.THIS_ENTITY);
		Vec3 pos = context.required(NeoApoliContextKeys.THIS_POS);

		context.getLevel().gameEvent(entity, gameEvent(), pos);

	}

}
