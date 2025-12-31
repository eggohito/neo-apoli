package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;

@EqualsAndHashCode
@Getter
public class ModifyInvisibilityPower extends Power {

	public static final MapCodec<ModifyInvisibilityPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Condition.CODEC.optionalFieldOf("invisible_to_condition", new ConstantMetaCondition(true)).forGetter(ModifyInvisibilityPower::getInvisibleToCondition))
		.and(BooleanProvider.CODEC.optionalFieldOf("render_armor", new ConstantBooleanProvider(true)).forGetter(ModifyInvisibilityPower::getRenderArmor))
		.and(BooleanProvider.CODEC.optionalFieldOf("render_outline", new ConstantBooleanProvider(true)).forGetter(ModifyInvisibilityPower::getRenderOutline))
		.apply(instance, ModifyInvisibilityPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyInvisibilityPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Condition.STREAM_CODEC, ModifyInvisibilityPower::getInvisibleToCondition,
		BooleanProvider.STREAM_CODEC, ModifyInvisibilityPower::getRenderArmor,
		BooleanProvider.STREAM_CODEC, ModifyInvisibilityPower::getRenderOutline,
		ModifyInvisibilityPower::new
	);

	private final Condition invisibleToCondition;
	private final BooleanProvider renderArmor;
	private final BooleanProvider renderOutline;

	public ModifyInvisibilityPower(Optional<Condition> activeCondition, Condition invisibleToCondition, BooleanProvider renderArmor, BooleanProvider renderOutline) {
		super(activeCondition);
		this.invisibleToCondition = invisibleToCondition;
		this.renderArmor = renderArmor;
		this.renderOutline = renderOutline;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_INVISIBILITY;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		getInvisibleToCondition().validate(validator.forChild(".invisible_to_condition"));
		getRenderArmor().validate(validator.forChild(".render_armor"));
		getRenderOutline().validate(validator.forChild(".render_outline"));
	}

	public static class Instance extends Power.Instance<ModifyInvisibilityPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyInvisibilityPower power) {
			super(holder, power);
		}

		public boolean isInvisibleTo(Context context) {
			return this.isActive(context)
				&& power.getInvisibleToCondition().test(context.forChild(".invisible_to_condition"));
		}

		public boolean isActiveAndShouldRenderArmor(Context context) {
			return this.isActive(context)
				&& power.getRenderArmor().next(context.forChild(".render_armor"));
		}

		public boolean isActiveAndShouldRenderOutline(Context context) {
			return this.isActive(context)
				&& power.getRenderOutline().next(context.forChild(".render_outline"));
		}

	}

	public static boolean doesApply(Context context, List<Instance> instances, BiPredicate<Instance, Context> tester, BooleanSupplier defaultValue) {

		for (var instance : instances) {

			Context instanceContext = new Context.Builder(context)
				.withValidator(instance.getValidator())
				.build(context.getLevel());

			try {

				if (instanceContext.markActive(instance) && tester.test(instance, instanceContext)) {
					return true;
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return defaultValue.getAsBoolean();

	}

	public static Context createContext(@NotNull Entity target, @Nullable Entity viewer) {
		return PowerTypes.MODIFY_INVISIBILITY.contextBuilder()
			.addNullable(NeoApoliContextKeys.ACTOR_ENTITY, viewer)
			.add(NeoApoliContextKeys.TARGET_ENTITY, target)
			.add(NeoApoliContextKeys.THIS_ENTITY, target)
			.add(NeoApoliContextKeys.THIS_POS, target.position())
			.build(target.level());
	}

}
