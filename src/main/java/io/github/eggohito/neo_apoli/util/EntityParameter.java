package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.function.ValueLists;

public enum EntityParameter implements StringIdentifiable {

	THIS("this", ContextParameters.THIS_ENTITY),
	ACTOR("actor", ContextParameters.ACTOR),
	TARGET("target", ContextParameters.TARGET);

	public static final Codec<EntityParameter> CODEC = StringIdentifiable.createBasicCodec(EntityParameter::values);
	public static final PacketCodec<ByteBuf, EntityParameter> PACKET_CODEC = PacketCodecs.indexed(ValueLists.createIndexToValueFunction(EntityParameter::ordinal, EntityParameter.values(), ValueLists.OutOfBoundsHandling.WRAP), EntityParameter::ordinal);

	private final String name;
	private final ContextParameter<Entity> parameter;
	
	EntityParameter(String name, ContextParameter<Entity> parameter) {
		this.name = name;
		this.parameter = parameter;
	}

	@Override
	public String asString() {
		return name;
	}

	public ContextParameter<Entity> getParameter() {
		return parameter;
	}

}
