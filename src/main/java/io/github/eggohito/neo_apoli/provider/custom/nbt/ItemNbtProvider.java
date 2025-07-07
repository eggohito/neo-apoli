package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.NbtProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameter;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode
@Data
public final class ItemNbtProvider extends NbtProvider {

	public static final MapCodec<ItemNbtProvider> CODEC = MapCodec.unit(ItemNbtProvider::new);
	public static final PacketCodec<RegistryByteBuf, ItemNbtProvider> PACKET_CODEC = PacketCodec.unit(new ItemNbtProvider());

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.ITEM;
	}

	@Override
	protected NbtElement impl(Context context) {

		RegistryOps<NbtElement> nbtOps = context.getWorld().getRegistryManager().getOps(NbtOps.INSTANCE);
		ItemStack stack = context.optional(ContextParameters.ITEM_STACK)
			.or(() -> context.optional(ContextParameters.STACK_REFERENCE).map(StackReference::get))
			.orElse(ItemStack.EMPTY);

		return ItemStack.CODEC.encodeStart(nbtOps, stack)
			.resultOrPartial(context.getReporter()::report)
			.orElseGet(NbtCompound::new);

	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);
		Set<ContextParameter<?>> anyAllowedParameters = Set.of(ContextParameters.ITEM_STACK, ContextParameters.STACK_REFERENCE);

		if (Collections.disjoint(reporter.getContextType().getAllowed(), anyAllowedParameters)) {
			reporter.report("Any of parameters [" + anyAllowedParameters.stream().map(ContextParameter::getId).map(Identifier::toString).collect(Collectors.joining(", ")) + "] are not provided in context for " + this.asDisplayString(false) + "!");
		}

	}

}
