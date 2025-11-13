package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.Tameable;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Objects;

public record IsOwnerBiEntityCondition() implements BiEntityCondition {

	public static final MapCodec<IsOwnerBiEntityCondition> CODEC = MapCodec.unit(IsOwnerBiEntityCondition::new);
	public static final PacketCodec<RegistryByteBuf, IsOwnerBiEntityCondition> PACKET_CODEC = PacketCodecUtil.unit(IsOwnerBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.IS_OWNER;
	}

	@Override
	public boolean test(Context context) {

		Entity actor = context.nullable(ContextParameters.ACTOR);
		Entity target = context.nullable(ContextParameters.TARGET);

		return actor != null
			&& this.isOwnedBy(target, actor);

	}

	private boolean isOwnedBy(Entity target, Entity actor) {
		return (target instanceof Tameable tameable && Objects.equals(actor, tameable.getOwner()))
			|| (target instanceof Ownable ownable && Objects.equals(actor, ownable.getOwner()));
	}

}
