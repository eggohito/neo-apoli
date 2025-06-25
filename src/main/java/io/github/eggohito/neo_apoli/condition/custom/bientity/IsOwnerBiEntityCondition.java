package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.Tameable;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Objects;

@EqualsAndHashCode(callSuper = false)
@Data
public final class IsOwnerBiEntityCondition extends BiEntityCondition {

	public static final MapCodec<IsOwnerBiEntityCondition> CODEC = MapCodec.unit(IsOwnerBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, IsOwnerBiEntityCondition> PACKET_CODEC = PacketCodec.unit(new IsOwnerBiEntityCondition());

	public IsOwnerBiEntityCondition() {

	}

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.IS_OWNER;
	}

	@Override
	protected boolean impl(Context context) {

		Entity actor = context.required(ContextParameters.ACTOR);
		Entity target = context.required(ContextParameters.TARGET);

		return (target instanceof Tameable tameable && Objects.equals(actor, tameable.getOwner()))
			|| (target instanceof Ownable ownable && Objects.equals(actor, ownable.getOwner()));

	}

}
