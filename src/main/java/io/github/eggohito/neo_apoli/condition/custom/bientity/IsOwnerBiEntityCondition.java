package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;

import java.util.Objects;

public record IsOwnerBiEntityCondition() implements BiEntityCondition {

	public static final MapCodec<IsOwnerBiEntityCondition> CODEC = MapCodec.unit(IsOwnerBiEntityCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, IsOwnerBiEntityCondition> STREAM_CODEC = StreamCodecUtil.unit(IsOwnerBiEntityCondition::new);

	@Override
	public BiEntityConditionType<?> getType() {
		return BiEntityConditionTypes.IS_OWNER;
	}

	@Override
	public boolean test(Context context) {

		Entity actor = context.nullable(NeoApoliContextKeys.ACTOR);
		Entity target = context.nullable(NeoApoliContextKeys.TARGET);

		return actor != null
			&& this.isOwnedBy(target, actor);

	}

	private boolean isOwnedBy(Entity target, Entity actor) {
		return (target instanceof OwnableEntity tameable && Objects.equals(actor, tameable.getOwner()))
			|| (target instanceof TraceableEntity ownable && Objects.equals(actor, ownable.getOwner()));
	}

}
