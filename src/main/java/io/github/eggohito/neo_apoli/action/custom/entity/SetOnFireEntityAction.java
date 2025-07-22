package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class SetOnFireEntityAction extends EntityAction {

	public static final MapCodec<SetOnFireEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("ticks").forGetter(SetOnFireEntityAction::ticks)
	).apply(instance, SetOnFireEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, SetOnFireEntityAction> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, SetOnFireEntityAction::ticks,
		SetOnFireEntityAction::new
	);

	private final NumberProvider ticks;

	public SetOnFireEntityAction(NumberProvider ticks) {
		this.ticks = ticks;
	}

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SET_ON_FIRE;
	}

	@Override
	protected void impl(Context context) {

		Context ticksContext = context.makeChild(".ticks");
		int ticks = ticks().nextInt(ticksContext);

		if (!ticksContext.hasErrors()) {
			context.required(ContextParameters.ENTITY).setOnFireForTicks(ticks);
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		ticks().validate(reporter.makeChild(".ticks"));
	}

}
