package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

@EqualsAndHashCode(callSuper = false)
@Data
public final class IsOfItemCondition extends ItemCondition {

	public static final MapCodec<IsOfItemCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Registries.ITEM.getEntryCodec().fieldOf("item").forGetter(IsOfItemCondition::item)
	).apply(instance, IsOfItemCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsOfItemCondition> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryEntry(RegistryKeys.ITEM), IsOfItemCondition::item,
		IsOfItemCondition::new
	);

	private final RegistryEntry<Item> item;

	public IsOfItemCondition(RegistryEntry<Item> item) {
		this.item = item;
	}

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.IS_OF;
	}

	@Override
	protected boolean impl(Context context) {
		return context.required(ContextParameters.ITEM_STACK).itemMatches(this.item());
	}

}
