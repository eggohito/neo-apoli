package io.github.eggohito.neo_apoli.provider.type.vec3d;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Vec3dProviderTypes {

	public static final Vec3dProviderType<BlockPositionVec3dProvider> BLOCK_POSITION = registerInternal("position/block", BlockPositionVec3dProvider.CODEC, BlockPositionVec3dProvider.PACKET_CODEC);
	public static final Vec3dProviderType<ConstantVec3dProvider> CONSTANT = registerInternal("constant", ConstantVec3dProvider.CODEC, ConstantVec3dProvider.PACKET_CODEC);
	public static final Vec3dProviderType<DynamicVec3dProvider> DYNAMIC = registerInternal("dynamic", DynamicVec3dProvider.CODEC, DynamicVec3dProvider.PACKET_CODEC);
	public static final Vec3dProviderType<EntityPositionVec3dProvider> ENTITY_POSITION = registerInternal("position/entity", EntityPositionVec3dProvider.CODEC, EntityPositionVec3dProvider.PACKET_CODEC);
	public static final Vec3dProviderType<OffsetVec3dProvider> OFFSET = registerInternal("offset", OffsetVec3dProvider.CODEC, OffsetVec3dProvider.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends Vec3dProvider> Vec3dProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends Vec3dProvider> Vec3dProviderType<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.VEC3D_PROVIDER_TYPE, id, new Vec3dProviderType<>(mapCodec, packetCodec));
	}

}
