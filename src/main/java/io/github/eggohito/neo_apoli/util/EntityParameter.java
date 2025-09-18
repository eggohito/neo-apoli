package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;

public enum EntityParameter implements StringIdentifiable {

	THIS("this", ContextParameters.ENTITY),
	ACTOR("actor", ContextParameters.ACTOR),
	TARGET("target", ContextParameters.TARGET),
	DAMAGING_ENTITY("damaging_entity", ContextParameters.DAMAGING_ENTITY),
	DIRECT_DAMAGE_ENTITY_SOURCE("direct_damaging_entity", ContextParameters.DIRECT_DAMAGING_ENTITY);

	public static final Codec<EntityParameter> CODEC = CodecUtil.enumType(EntityParameter.class);
	public static final PacketCodec<ByteBuf, EntityParameter> PACKET_CODEC = PacketCodecUtil.enumType(EntityParameter.class);

	private final String name;
	@Getter
	private final ContextParameter<Entity> parameter;
	
	EntityParameter(String name, ContextParameter<Entity> parameter) {
		this.name = name;
		this.parameter = parameter;
	}

	@Override
	public String asString() {
		return name;
	}

}
