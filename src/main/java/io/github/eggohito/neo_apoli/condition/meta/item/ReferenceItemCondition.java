package io.github.eggohito.neo_apoli.condition.meta.item;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

@EqualsAndHashCode(callSuper = false)
@Data
public final class ReferenceItemCondition extends ItemCondition implements ReferenceMetaCondition<ItemCondition> {

	public static final MapCodec<ReferenceItemCondition> CODEC = ReferenceMetaCondition.codec(ReferenceItemCondition::new);
	public static final PacketCodec<RegistryByteBuf, ReferenceItemCondition> PACKET_CODEC = ReferenceMetaCondition.packetCodec(ReferenceItemCondition::new);

	private final Identifier value;

	public ReferenceItemCondition(Identifier value) {
		this.value = value;
	}

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.REFERENCE;
	}

	@Override
	public boolean impl(Context context) {
		return ReferenceMetaCondition.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		ReferenceMetaCondition.super.validate(reporter);
	}

}
