package io.github.eggohito.neo_apoli.condition.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.AllOfMetaCondition;
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
public final class AllOfItemCondition extends ItemCondition implements AllOfMetaCondition<ItemCondition> {

	public static final MapCodec<AllOfItemCondition> CODEC = MapCodecUtil.lazy(AllOfItemCondition.class.getSimpleName(), () -> AllOfMetaCondition.codec(ItemCondition.CODEC, AllOfItemCondition::new));
	public static final PacketCodec<RegistryByteBuf, AllOfItemCondition> PACKET_CODEC = PacketCodecUtil.lazy(AllOfItemCondition.class.getSimpleName(), () -> AllOfMetaCondition.packetCodec(ItemCondition.PACKET_CODEC, AllOfItemCondition::new));

	private final List<ItemCondition> conditions;

	public AllOfItemCondition(List<ItemCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.ALL_OF;
	}

	@Override
	public boolean impl(Context context) {
		return AllOfMetaCondition.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		AllOfMetaCondition.super.validate(reporter);
	}

}
