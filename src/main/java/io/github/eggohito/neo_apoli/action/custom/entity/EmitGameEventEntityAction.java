package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.context.entity.EntityActionContext;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.event.GameEvent;

public record EmitGameEventEntityAction(RegistryEntry<GameEvent> gameEvent) implements EntityAction {

	public static final MapCodec<EmitGameEventEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Registries.GAME_EVENT.getEntryCodec().fieldOf("game_event").forGetter(EmitGameEventEntityAction::gameEvent)
	).apply(instance, EmitGameEventEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, EmitGameEventEntityAction> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryEntry(RegistryKeys.GAME_EVENT), EmitGameEventEntityAction::gameEvent,
		EmitGameEventEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EMIT_GAME_EVENT;
	}

	@Override
	public void execute(ErrorReporter reporter, EntityActionContext context) {
		context.entity().ifPresent(entity -> entity.emitGameEvent(gameEvent(), entity));
	}

}
