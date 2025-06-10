package io.github.eggohito.neo_apoli.particle.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public final class NeoApoliParticleTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<ParticleEffect> EFFECT_CODEC = RegistryUtil.createAliasedCodec(Registries.PARTICLE_TYPE, ALIASES).dispatch("type", ParticleEffect::getType, ParticleType::getCodec);
	public static final PacketCodec<RegistryByteBuf, ParticleEffect> EFFECT_PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.PARTICLE_TYPE).dispatch(ParticleEffect::getType, ParticleType::getPacketCodec);

	public static final SimpleParticleType NOTHING = registerInternal("nothing", false);

	public static void registerAll() {

	}

	private static SimpleParticleType registerInternal(String name, boolean alwaysShow) {
		return register(NeoApoli.id(name), alwaysShow);
	}

	public static SimpleParticleType register(Identifier id, boolean alwaysShow) {
		return Registry.register(Registries.PARTICLE_TYPE, id, FabricParticleTypes.simple(alwaysShow));
	}

	private static <P extends ParticleEffect> ParticleType<P> registerInternal(String name, boolean alwaysShow, Function<ParticleType<P>, MapCodec<P>> codecGetter, Function<ParticleType<P>, PacketCodec<? super RegistryByteBuf, P>> packetCodecGetter) {
		return register(NeoApoli.id(name), alwaysShow, codecGetter, packetCodecGetter);
	}

	public static <P extends ParticleEffect> ParticleType<P> register(Identifier id, boolean alwaysShow, Function<ParticleType<P>, MapCodec<P>> codecGetter, Function<ParticleType<P>, PacketCodec<? super RegistryByteBuf, P>> packetCodecGetter) {
		return Registry.register(Registries.PARTICLE_TYPE, id, new ParticleType<P>(alwaysShow) {

			@Override
			public MapCodec<P> getCodec() {
				return codecGetter.apply(this);
			}

			@Override
			public PacketCodec<? super RegistryByteBuf, P> getPacketCodec() {
				return packetCodecGetter.apply(this);
			}

		});
	}

}
