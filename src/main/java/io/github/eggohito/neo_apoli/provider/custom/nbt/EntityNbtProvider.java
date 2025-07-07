package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NbtProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

@EqualsAndHashCode
@Data
public final class EntityNbtProvider extends NbtProvider {

	public static final MapCodec<EntityNbtProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityParameter.CODEC.fieldOf("source").forGetter(EntityNbtProvider::source)
	).apply(instance, EntityNbtProvider::new));

	public static final PacketCodec<RegistryByteBuf, EntityNbtProvider> PACKET_CODEC = PacketCodec.tuple(
		EntityParameter.PACKET_CODEC, EntityNbtProvider::source,
		EntityNbtProvider::new
	);

	private final EntityParameter source;

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.ENTITY;
	}

	@Override
	protected NbtElement impl(Context context) {
		return context.required(source().getParameter()).writeNbt(new NbtCompound());
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(source().getParameter());
	}

}
