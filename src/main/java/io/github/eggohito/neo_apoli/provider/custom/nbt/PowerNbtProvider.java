package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public record PowerNbtProvider(PowerIdentifier power, ContextParameter<Entity> entity) implements NbtProvider {

	public static final MapCodec<PowerNbtProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		PowerIdentifier.CODEC.fieldOf("power").forGetter(PowerNbtProvider::power),
		NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(PowerNbtProvider::entity)
	).apply(instance, PowerNbtProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PowerNbtProvider> STREAM_CODEC = StreamCodec.composite(
		PowerIdentifier.STREAM_CODEC, PowerNbtProvider::power,
		NeoApoliContextParams.StreamCodecs.ENTITY, PowerNbtProvider::entity,
		PowerNbtProvider::new
	);

	@Override
	public @NotNull NbtProviderType<?> getType() {
		return NbtProviderTypes.POWER;
	}

	@Override
	public @NotNull Tag nextTag(Context context) {

		if (!context.hasParameter(entity())) {
			context.reportProblem("Couldn't get and provide NBT of " + power().asDisplayString(false) + " from non-existent entity from parameter \"" + entity().name() + "\"!");
		}

		return context.getOptional(entity())
			.flatMap(Powers::getOptional)
			.flatMap(powers -> this.getAndCreate(context, powers))
			.orElseGet(CompoundTag::new);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

	@Override
	public void validate(Context.Validator validator) {
		NbtProvider.super.validate(validator);
		this.power().validate(validator.forChild(".power"));
	}

	private Optional<Tag> getAndCreate(Context context, Powers powers) {
		RegistryOps<Tag> ops = context.level().registryAccess().createSerializationContext(NbtOps.INSTANCE);
		return powers.getOptionalInstance(this.power())
			.flatMap(instance -> instance.encodeData(ops)
				.resultOrPartial(err -> context.reportProblem("Error while encoding and providing data of " + power().asDisplayString(false) + ": " + err)));
	}

}
