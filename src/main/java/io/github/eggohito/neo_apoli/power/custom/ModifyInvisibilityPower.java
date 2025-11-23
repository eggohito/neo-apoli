package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.ConstantCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

@Getter
public class ModifyInvisibilityPower extends Power {

	public static final MapCodec<ModifyInvisibilityPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Condition.CODEC.optionalFieldOf("invisible_to_condition", new ConstantCondition(true)).forGetter(ModifyInvisibilityPower::getInvisibleToCondition))
		.and(BooleanProvider.CODEC.optionalFieldOf("render_armor", new ConstantBooleanProvider(true)).forGetter(ModifyInvisibilityPower::getRenderArmor))
		.and(BooleanProvider.CODEC.optionalFieldOf("render_outline", new ConstantBooleanProvider(true)).forGetter(ModifyInvisibilityPower::getRenderOutline))
		.apply(instance, ModifyInvisibilityPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyInvisibilityPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
		Condition.PACKET_CODEC, ModifyInvisibilityPower::getInvisibleToCondition,
		BooleanProvider.PACKET_CODEC, ModifyInvisibilityPower::getRenderArmor,
		BooleanProvider.PACKET_CODEC, ModifyInvisibilityPower::getRenderOutline,
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
	public void validate(ContextAware.ErrorReporter reporter) {
		super.validate(reporter);
		getInvisibleToCondition().validate(reporter.makeChild(".invisible_to_condition"));
		getRenderArmor().validate(reporter.makeChild(".render_armor"));
		getRenderOutline().validate(reporter.makeChild(".render_outline"));
	}

	public static class Instance extends Power.Instance<ModifyInvisibilityPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyInvisibilityPower power) {
			super(holder, power);
		}

		public boolean isInvisibleTo(Context context) {
			return this.isActive(context)
				&& power.getInvisibleToCondition().test(context.makeChild(".invisible_to_condition"));
		}

		public boolean shouldRenderArmor(Context context) {
			return this.isActive(context)
				&& power.getRenderArmor().next(context.makeChild(".render_armor"));
		}

		public boolean shouldRenderOutline(Context context) {
			return this.isActive(context)
				&& power.getRenderOutline().next(context.makeChild(".render_outline"));
		}

	}

	public static boolean doesApply(Context context, BiPredicate<Instance, Context> tester) {

		Entity entity = context.nullable(NeoApoliContextParameters.THIS_ENTITY);
		List<Instance> instances = PowersComponent.getInstances(entity, Instance.class);

		for (var instance : instances) {

			ErrorReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

			try {

				if (instanceContext.markActive(instance) && tester.test(instance, instanceContext)) {
					return true;
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return false;

	}

	public static Context createContext(@NotNull Entity target, @Nullable Entity viewer) {
		return PowerTypes.MODIFY_INVISIBILITY.contextBuilder()
			.addNullable(NeoApoliContextParameters.ACTOR, viewer)
			.add(NeoApoliContextParameters.TARGET, target)
			.add(NeoApoliContextParameters.THIS_ENTITY, target)
			.add(NeoApoliContextParameters.ENTITY_POS, target.getPos())
			.build(target.getWorld());
	}

}
