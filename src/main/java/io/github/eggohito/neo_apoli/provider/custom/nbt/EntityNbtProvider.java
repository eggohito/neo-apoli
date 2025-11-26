package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public record EntityNbtProvider(EntityTarget source) implements NbtProvider {

	public static final MapCodec<EntityNbtProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityTarget.CODEC.fieldOf("source").forGetter(EntityNbtProvider::source)
	).apply(instance, EntityNbtProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EntityNbtProvider> STREAM_CODEC = StreamCodec.composite(
		EntityTarget.STREAM_CODEC, EntityNbtProvider::source,
		EntityNbtProvider::new
	);

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.ENTITY;
	}

	@Override
	public @NotNull Tag next(Context context) {

		ContextKey<Entity> parameter = source().getParameter();
		Optional<Entity> optEntity = context.optional(parameter);

		if (optEntity.isEmpty()) {
			context.getReporter().report("Couldn't get and provide NBT of entity from '" + parameter.name() + "' parameter, as it doesn't exist!");
		}

		return optEntity
			.map(entity -> entity.saveWithoutId(new CompoundTag()))
			.orElseGet(CompoundTag::new);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(source().getParameter());
	}

}
