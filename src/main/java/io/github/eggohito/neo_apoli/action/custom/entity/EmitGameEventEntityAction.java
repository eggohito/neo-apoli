package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.event.GameEvent;

@EqualsAndHashCode(callSuper = false)
@Data
public final class EmitGameEventEntityAction extends EntityAction {

	public static final MapCodec<EmitGameEventEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Registries.GAME_EVENT.getEntryCodec().fieldOf("game_event").forGetter(EmitGameEventEntityAction::gameEvent)
	).apply(instance, EmitGameEventEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, EmitGameEventEntityAction> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryEntry(RegistryKeys.GAME_EVENT), EmitGameEventEntityAction::gameEvent,
		EmitGameEventEntityAction::new
	);

	private final RegistryEntry<GameEvent> gameEvent;

	public EmitGameEventEntityAction(RegistryEntry<GameEvent> gameEvent) {
		this.gameEvent = gameEvent;
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EMIT_GAME_EVENT;
	}

	@Override
	protected void impl(Context context) {
		context.getWorld().emitGameEvent(context.required(ContextParameters.THIS_ENTITY), gameEvent(), context.required(ContextParameters.POSITION));
	}

}
