package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class IsPowerActiveEntityCondition extends EntityCondition {

	public static final MapCodec<IsPowerActiveEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		PowerReference.CODEC.fieldOf("power").forGetter(IsPowerActiveEntityCondition::power)
	).apply(instance, IsPowerActiveEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsPowerActiveEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		PowerReference.PACKET_CODEC, IsPowerActiveEntityCondition::power,
		IsPowerActiveEntityCondition::new
	);

	private final PowerReference power;

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_POWER_ACTIVE;
	}

	@Override
	protected boolean impl(Context context) {

		Entity entity = context.required(ContextParameters.ENTITY);
		PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(entity);

		return powersComponent.hasInstance(this.power())
			&& powersComponent.getInstance(this.power()).isActive(context);

	}

}
