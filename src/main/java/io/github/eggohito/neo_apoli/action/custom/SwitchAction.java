package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import io.github.eggohito.neo_apoli.util.Case;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.List;

public record SwitchAction(List<Case<Condition, Action>> cases, Action defaultAction) implements Action {

	public static final MapCodec<SwitchAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Case.codec(Condition.CODEC.fieldOf("condition"), Action.CODEC.fieldOf("action")).listOf().fieldOf("cases").forGetter(SwitchAction::cases),
		Action.CODEC.optionalFieldOf("default", NothingAction.INSTANCE).forGetter(SwitchAction::defaultAction)
	).apply(instance, SwitchAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, SwitchAction> STREAM_CODEC = StreamCodec.composite(
		Case.streamCodec(Condition.STREAM_CODEC, Action.STREAM_CODEC).apply(ByteBufCodecs.list()), SwitchAction::cases,
		Action.STREAM_CODEC, SwitchAction::defaultAction,
		SwitchAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.SWITCH;
	}

	@Override
	public void execute(Context context) {

		MutableBoolean continueCondition = new MutableBoolean(true);
		MiscUtil.iterateList(
			cases(),
			(index, aCase) -> {

				Context caseContext = context.forChild(".cases[" + index + "]");
				boolean result = aCase.condition().test(caseContext.forChild(".condition"));

				if (result) {
					aCase.value().execute(caseContext.forChild(".action"));
				}

				continueCondition.setValue(!result);

			},
			continueCondition::isTrue
		);

		if (continueCondition.isTrue()) {
			defaultAction().execute(context.forChild(".default"));
		}

	}

	@Override
	public void validate(Context.Validator validator) {

		Action.super.validate(validator);
		MiscUtil.iterateList(
			cases(),
			(index, aCase) -> {

				Context.Validator caseValidator = validator.forChild(".cases[" + index + "]");

				aCase.condition().validate(caseValidator.forChild(".condition"));
				aCase.value().validate(caseValidator.forChild(".action"));

			}
		);

		defaultAction().validate(validator.forChild(".default"));

	}

}
