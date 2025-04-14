package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.context.entity.EntityActionContext;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record SetOnFireEntityAction(int ticks) implements EntityAction {

	public static final MapCodec<SetOnFireEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("ticks").forGetter(SetOnFireEntityAction::ticks)
	).apply(instance, SetOnFireEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, SetOnFireEntityAction> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.INTEGER, SetOnFireEntityAction::ticks,
		SetOnFireEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SET_ON_FIRE;
	}

	@Override
	public void accept(EntityActionContext context) {
		context.entity().ifPresent(entity -> entity.setOnFireForTicks(ticks()));
	}

}
