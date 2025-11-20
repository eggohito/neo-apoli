package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public record ItemNbtProvider() implements NbtProvider {

	public static final MapCodec<ItemNbtProvider> CODEC = MapCodec.unit(ItemNbtProvider::new);
	public static final PacketCodec<RegistryByteBuf, ItemNbtProvider> PACKET_CODEC = PacketCodecUtil.unit(ItemNbtProvider::new);

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.ITEM;
	}

	@Override
	public @NotNull NbtElement next(Context context) {

		RegistryOps<NbtElement> ops = context.getWorld().getRegistryManager().getOps(NbtOps.INSTANCE);
		Optional<ItemStack> optStack = context.optional(NeoApoliContextParameters.ITEM_STACK);

		if (optStack.isEmpty()) {
			context.getReporter().report("Couldn't encode and provide non-existent item stack as NBT!");
		}

		return optStack
			.flatMap(stack -> ItemStack.OPTIONAL_CODEC.encodeStart(ops, stack).resultOrPartial(context.getReporter()::report))
			.orElseGet(NbtCompound::new);

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParameters.ITEM_STACK);
	}

}
