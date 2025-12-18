package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
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

public record ItemNbtProvider() implements NbtProvider {

	public static final MapCodec<ItemNbtProvider> CODEC = MapCodec.unit(ItemNbtProvider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ItemNbtProvider> STREAM_CODEC = StreamCodecUtil.unit(ItemNbtProvider::new);

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.ITEM;
	}

	@Override
	public @NotNull Tag next(Context context) {

		RegistryOps<Tag> ops = context.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE);
		Optional<ItemStack> optStack = context.optional(NeoApoliContextKeys.ITEM_STACK);

		if (optStack.isEmpty()) {
			context.getValidator().report("Couldn't encode and provide non-existent item stack as NBT!");
		}

		return optStack
			.flatMap(stack -> ItemStack.OPTIONAL_CODEC.encodeStart(ops, stack).resultOrPartial(context.getValidator()::report))
			.orElseGet(CompoundTag::new);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.ITEM_STACK);
	}

}
