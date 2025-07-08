package io.github.eggohito.neo_apoli.condition.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class InvertedItemCondition extends ItemCondition implements InvertedMetaCondition<ItemCondition> {

	public static final MapCodec<InvertedItemCondition> CODEC = MapCodecUtil.lazy(InvertedItemCondition.class.getSimpleName(), () -> InvertedMetaCondition.codec(ItemCondition.CODEC, InvertedItemCondition::new));
	public static final PacketCodec<RegistryByteBuf, InvertedItemCondition> PACKET_CODEC = PacketCodecUtil.lazy(InvertedItemCondition.class.getSimpleName(), () -> InvertedMetaCondition.packetCodec(ItemCondition.PACKET_CODEC, InvertedItemCondition::new));

	private final ItemCondition condition;

	public InvertedItemCondition(ItemCondition condition) {
		this.condition = condition;
	}

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.INVERTED;
	}

	@Override
	public boolean impl(Context context) {
		return InvertedMetaCondition.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		InvertedMetaCondition.super.validate(reporter);
	}

}
