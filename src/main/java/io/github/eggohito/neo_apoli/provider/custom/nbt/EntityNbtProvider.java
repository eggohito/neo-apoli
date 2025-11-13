package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public record EntityNbtProvider(EntityTarget source) implements NbtProvider {

	public static final MapCodec<EntityNbtProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityTarget.CODEC.fieldOf("source").forGetter(EntityNbtProvider::source)
	).apply(instance, EntityNbtProvider::new));

	public static final PacketCodec<RegistryByteBuf, EntityNbtProvider> PACKET_CODEC = PacketCodec.tuple(
		EntityTarget.PACKET_CODEC, EntityNbtProvider::source,
		EntityNbtProvider::new
	);

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.ENTITY;
	}

	@Override
	public @NotNull NbtElement next(Context context) {

		ContextParameter<Entity> parameter = source().getParameter();
		Optional<Entity> optEntity = context.optional(parameter);

		if (optEntity.isEmpty()) {
			context.getReporter().report("Couldn't get and provide NBT of entity from '" + parameter.getId() + "' parameter, as it doesn't exist!");
		}

		return optEntity
			.map(entity -> entity.writeNbt(new NbtCompound()))
			.orElseGet(NbtCompound::new);

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(source().getParameter());
	}

}
