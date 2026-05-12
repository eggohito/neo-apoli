package io.github.eggohito.neo_apoli.registry.provider;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.vec3.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class NeoApoliVec3ProviderTypes {

	public static final Vec3Provider.Type<BlockPositionVec3Provider> BLOCK_POSITION = registerInternal("position/block", BlockPositionVec3Provider.MAP_CODEC, BlockPositionVec3Provider.STREAM_CODEC);
	public static final Vec3Provider.Type<ConstantVec3Provider> CONSTANT = registerInternal("constant", ConstantVec3Provider.MAP_CODEC, ConstantVec3Provider.STREAM_CODEC);
	public static final Vec3Provider.Type<DynamicVec3Provider> DYNAMIC = registerInternal("dynamic", DynamicVec3Provider.MAP_CODEC, DynamicVec3Provider.STREAM_CODEC);
	public static final Vec3Provider.Type<EntityPositionVec3Provider> ENTITY_POSITION = registerInternal("position/entity", EntityPositionVec3Provider.MAP_CODEC, EntityPositionVec3Provider.STREAM_CODEC);
	public static final Vec3Provider.Type<OffsetVec3Provider> OFFSET = registerInternal("offset", OffsetVec3Provider.MAP_CODEC, OffsetVec3Provider.STREAM_CODEC);
	public static final Vec3Provider.Type<VelocityVec3Provider> VELOCITY = registerInternal("entity/velocity", VelocityVec3Provider.MAP_CODEC, VelocityVec3Provider.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <P extends Vec3Provider> Vec3Provider.Type<P> registerInternal(String path, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <P extends Vec3Provider> Vec3Provider.Type<P> register(ResourceLocation id, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec) {
		return Registry.register(NeoApoliRegistries.VEC3_PROVIDER_TYPE, id, new Vec3Provider.Type<>(mapCodec, streamCodec));
	}

}
