package io.github.eggohito.neo_apoli.action.meta.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.ExplodeMetaAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode(callSuper = false)
@Data
public final class ExplodeEntityAction extends EntityAction implements ExplodeMetaAction {

	public static final MapCodec<ExplodeEntityAction> CODEC = ExplodeMetaAction.codec(ExplodeEntityAction::new);
	public static final PacketCodec<RegistryByteBuf, ExplodeEntityAction> PACKET_CODEC = ExplodeMetaAction.packetCodec(ExplodeEntityAction::new);

	private final BiEntityCondition damageableBiEntityCondition;
	private final BlockCondition destructibleBlockCondition;

	private final ExplosionProperty property;
	private final ExplosionDisplay display;

	public ExplodeEntityAction(BiEntityCondition damageableBiEntityCondition, BlockCondition destructibleBlockCondition, ExplosionProperty property, ExplosionDisplay display) {
		this.damageableBiEntityCondition = damageableBiEntityCondition;
		this.destructibleBlockCondition = destructibleBlockCondition;
		this.property = property;
		this.display = display;
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.EXPLODE;
	}

	@Override
	public void impl(Context context) {
		ExplodeMetaAction.super.impl(context);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		ExplodeMetaAction.super.validate(reporter);
	}

}
