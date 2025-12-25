package io.github.eggohito.neo_apoli.particle.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public final class NeoApoliParticleTypes {

	public static final RegistryFixedAlias<ParticleType<?>> ALIASES = RegistryFixedAlias.of(BuiltInRegistries.PARTICLE_TYPE);

	public static final Codec<ParticleType<?>> CODEC = ALIASES.createCodec();
	public static final StreamCodec<RegistryFriendlyByteBuf, ParticleType<?>> STREAM_CODEC = ByteBufCodecs.registry(Registries.PARTICLE_TYPE);

	public static final Codec<ParticleOptions> OPTIONS_CODEC = CODEC.dispatch(ParticleOptions::getType, ParticleType::codec);
	public static final StreamCodec<RegistryFriendlyByteBuf, ParticleOptions> OPTIONS_STREAM_CODEC = STREAM_CODEC.dispatch(ParticleOptions::getType, ParticleType::streamCodec);

	public static final SimpleParticleType NOTHING = registerInternal("nothing", false);

	public static void registerAll() {

	}

	private static SimpleParticleType registerInternal(String name, boolean alwaysShow) {
		return register(NeoApoli.id(name), alwaysShow);
	}

	public static SimpleParticleType register(ResourceLocation id, boolean alwaysShow) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, id, FabricParticleTypes.simple(alwaysShow));
	}

	private static <P extends ParticleOptions> ParticleType<P> registerInternal(String name, boolean alwaysShow, Function<ParticleType<P>, MapCodec<P>> codecGetter, Function<ParticleType<P>, StreamCodec<? super RegistryFriendlyByteBuf, P>> packetCodecGetter) {
		return register(NeoApoli.id(name), alwaysShow, codecGetter, packetCodecGetter);
	}

	public static <P extends ParticleOptions> ParticleType<P> register(ResourceLocation id, boolean alwaysShow, Function<ParticleType<P>, MapCodec<P>> codecGetter, Function<ParticleType<P>, StreamCodec<? super RegistryFriendlyByteBuf, P>> packetCodecGetter) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, id, new ParticleType<P>(alwaysShow) {

			@Override
			public MapCodec<P> codec() {
				return codecGetter.apply(this);
			}

			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, P> streamCodec() {
				return packetCodecGetter.apply(this);
			}

		});
	}

}
