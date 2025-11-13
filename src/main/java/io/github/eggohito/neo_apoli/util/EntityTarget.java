package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;

//	TODO: Replace this with a type-specific codec dispatch and a registry for context parameters
public enum EntityTarget implements StringIdentifiable {

	THIS("this", ContextParameters.THIS_ENTITY),
	ACTOR("actor", ContextParameters.ACTOR),
	TARGET("target", ContextParameters.TARGET),
	DAMAGING_ENTITY("damaging_entity", ContextParameters.DAMAGING_ENTITY),
	DIRECT_DAMAGE_ENTITY_SOURCE("direct_damaging_entity", ContextParameters.DIRECT_DAMAGING_ENTITY);

	public static final Codec<EntityTarget> CODEC = CodecUtil.enumType(EntityTarget.class);
	public static final PacketCodec<ByteBuf, EntityTarget> PACKET_CODEC = PacketCodecUtil.enumType(EntityTarget.class);

	private final String name;
	@Getter
	private final ContextParameter<Entity> parameter;

	EntityTarget(String name, ContextParameter<Entity> parameter) {
		this.name = name;
		this.parameter = parameter;
	}

	@Override
	public String asString() {
		return name;
	}

}
