package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.ConstantCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;

@EqualsAndHashCode
@Getter
public class ModifyInvisibilityPower extends Power {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

	public static final MapCodec<ModifyInvisibilityPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Condition.CODEC.optionalFieldOf("invisible_to_condition", new ConstantCondition(true)).forGetter(ModifyInvisibilityPower::getInvisibleToCondition))
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

		public Context createContext(@Nullable Entity viewer) {
			return this.createHolderContextBuilder()
				.withNullable(NeoApoliContextParams.ACTOR_ENTITY, viewer)
				.withRequired(NeoApoliContextParams.TARGET_ENTITY, holder)
				.buildWithRequirements(holder.level(), PowerTypes.MODIFY_INVISIBILITY.keySet());
		}

		public boolean isInvisibleTo(Context context) {
			return this.isActive(context)
				&& power.getInvisibleToCondition().test(context.forChild(".invisible_to_condition"));
		}

		public boolean isActiveAndShouldRenderArmor(Context context) {
			return this.isActive(context)
				&& power.getRenderArmor().nextBoolean(context.forChild(".render_armor"));
		}

		public boolean isActiveAndShouldRenderOutline(Context context) {
			return this.isActive(context)
				&& power.getRenderOutline().nextBoolean(context.forChild(".render_outline"));
		}

	}

	public static boolean modify(Entity holder, @Nullable Entity viewer, BiPredicate<Instance, Context> tester, BooleanSupplier defaultValue) {

		for (var instance : PowersComponent.getInstances(holder, Instance.class)) {

			Context context = instance.createContext(viewer);

			try {

				if (VISITOR.push(instance) && tester.test(instance, context)) {
					return true;
				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		return defaultValue.getAsBoolean();

	}

}
