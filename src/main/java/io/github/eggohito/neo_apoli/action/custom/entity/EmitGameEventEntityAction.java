package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;

public record EmitGameEventEntityAction(RegistryEntry<GameEvent> gameEvent) implements EntityAction {

	public static final MapCodec<EmitGameEventEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(GameEvent.CODEC.fieldOf("game_event").forGetter(EmitGameEventEntityAction::gameEvent))
		.apply(instance, EmitGameEventEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, EmitGameEventEntityAction> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryEntry(RegistryKeys.GAME_EVENT), EmitGameEventEntityAction::gameEvent,
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

		Entity entity = context.required(ContextParameters.THIS_ENTITY);
		Vec3d pos = context.required(ContextParameters.ENTITY_POS);

		context.getWorld().emitGameEvent(entity, gameEvent(), pos);

	}

}
