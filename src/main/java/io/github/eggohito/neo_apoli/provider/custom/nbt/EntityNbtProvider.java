package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNbtProviderTypes;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record EntityNbtProvider(Context.Parameter<Entity> entity) implements NbtProvider {

	public static final MapCodec<EntityNbtProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(EntityNbtProvider::entity)
	).apply(instance, EntityNbtProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EntityNbtProvider> STREAM_CODEC = StreamCodec.composite(
		NeoApoliContextParams.StreamCodecs.ENTITY, EntityNbtProvider::entity,
		EntityNbtProvider::new
	);

	@Override
	public @NotNull NbtProvider.Type<?> getType() {
		return NeoApoliNbtProviderTypes.ENTITY;
	}

	@Override
	public @NotNull Tag nextTag(Context context) {

		if (!context.hasParameter(entity())) {
			context.reportProblem("Couldn't get and provide NBT from non-existent entity from parameter \"" + entity().name() + "\"!");
		}

		return context.getOptional(entity())
			.map(NbtPredicate::getEntityTagToCompare)
			.orElseGet(CompoundTag::new);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

}
