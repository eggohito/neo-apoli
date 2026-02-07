package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
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
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public enum ItemNbtProvider implements NbtProvider {

	INSTANCE;

	public static final MapCodec<ItemNbtProvider> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, ItemNbtProvider> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.ITEM;
	}

	@Override
	public @NotNull Tag next(Context context) {

		RegistryOps<Tag> ops = context.level().registryAccess().createSerializationContext(NbtOps.INSTANCE);
		Optional<ItemStack> optStack = context.getOptional(NeoApoliContextParams.ITEM_STACK);

		if (optStack.isEmpty()) {
			context.reportProblem("Couldn't encode and provide non-existent item stack as NBT!");
		}

		return optStack
			.flatMap(stack -> ItemStack.OPTIONAL_CODEC.encodeStart(ops, stack).resultOrPartial(context::reportProblem))
			.orElseGet(CompoundTag::new);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.ITEM_STACK);
	}

}
