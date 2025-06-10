package io.github.eggohito.neo_apoli.particle;

import io.github.eggohito.neo_apoli.particle.type.NeoApoliParticleTypes;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public record NothingParticleEffect() implements ParticleEffect {

	@Override
	public ParticleType<?> getType() {
		return NeoApoliParticleTypes.NOTHING;
	}

}
