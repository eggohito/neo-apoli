package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;

//	TODO: Replace this with a type-specific codec dispatch and a registry for context parameters
public enum EntityTarget implements StringRepresentable {

	THIS("this", NeoApoliContextKeys.THIS_ENTITY),
	ACTOR("actor", NeoApoliContextKeys.ACTOR),
	TARGET("target", NeoApoliContextKeys.TARGET),
	DAMAGING_ENTITY("damaging_entity", NeoApoliContextKeys.DAMAGING_ENTITY),
	DIRECT_DAMAGE_ENTITY_SOURCE("direct_damaging_entity", NeoApoliContextKeys.DIRECT_DAMAGING_ENTITY);

	public static final Codec<EntityTarget> CODEC = CodecUtil.enumType(EntityTarget.class);
	public static final StreamCodec<ByteBuf, EntityTarget> STREAM_CODEC = StreamCodecUtil.enumType(EntityTarget.class);

	private final String name;
	@Getter
	private final ContextKey<Entity> parameter;

	EntityTarget(String name, ContextKey<Entity> parameter) {
		this.name = name;
		this.parameter = parameter;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

}
