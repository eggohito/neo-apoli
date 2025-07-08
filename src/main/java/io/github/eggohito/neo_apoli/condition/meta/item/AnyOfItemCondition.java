package io.github.eggohito.neo_apoli.condition.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.List;

@EqualsAndHashCode
@Data
public final class AnyOfItemCondition extends ItemCondition implements AnyOfMetaCondition<ItemCondition> {

	public static final MapCodec<AnyOfItemCondition> CODEC = MapCodecUtil.lazy(AnyOfItemCondition.class.getSimpleName(), () -> AnyOfMetaCondition.codec(ItemCondition.CODEC, AnyOfItemCondition::new));
	public static final PacketCodec<RegistryByteBuf, AnyOfItemCondition> PACKET_CODEC = PacketCodecUtil.lazy(AnyOfItemCondition.class.getSimpleName(), () -> AnyOfMetaCondition.packetCodec(ItemCondition.PACKET_CODEC, AnyOfItemCondition::new));

	private final List<ItemCondition> conditions;

	public AnyOfItemCondition(List<ItemCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.ANY_OF;
	}

	@Override
	public boolean impl(Context context) {
		return AnyOfMetaCondition.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		AnyOfMetaCondition.super.validate(reporter);
	}

}
