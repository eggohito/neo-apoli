package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.meta.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
public class ModifyInvisibilityPower extends Power {

	public static final MapCodec<ModifyInvisibilityPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(BiEntityCondition.CODEC.optionalFieldOf("bientity_condition", new ConstantBiEntityCondition(true)).forGetter(ModifyInvisibilityPower::getBiEntityCondition))
		.and(BooleanProvider.CODEC.optionalFieldOf("render_armor", new ConstantBooleanProvider(true)).forGetter(ModifyInvisibilityPower::getRenderArmorProvider))
		.and(BooleanProvider.CODEC.optionalFieldOf("render_outline", new ConstantBooleanProvider(true)).forGetter(ModifyInvisibilityPower::getRenderOutlineProvider))
		.apply(instance, ModifyInvisibilityPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyInvisibilityPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			BiEntityCondition.PACKET_CODEC.encode(buf, power.getBiEntityCondition());
			BooleanProvider.PACKET_CODEC.encode(buf, power.getRenderArmorProvider());
			BooleanProvider.PACKET_CODEC.encode(buf, power.getRenderOutlineProvider());
		},
		(buf, properties, condition) -> new ModifyInvisibilityPower(properties, condition,
			BiEntityCondition.PACKET_CODEC.decode(buf),
			BooleanProvider.PACKET_CODEC.decode(buf),
			BooleanProvider.PACKET_CODEC.decode(buf)
		)
	);

	private final BiEntityCondition biEntityCondition;

	private final BooleanProvider renderArmorProvider;
	private final BooleanProvider renderOutlineProvider;

	public ModifyInvisibilityPower(Properties properties, Optional<EntityCondition> activeCondition, BiEntityCondition biEntityCondition, BooleanProvider renderArmorProvider, BooleanProvider renderOutlineProvider) {
		super(properties, activeCondition);
		this.biEntityCondition = biEntityCondition;
		this.renderArmorProvider = renderArmorProvider;
		this.renderOutlineProvider = renderOutlineProvider;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_INVISIBILITY;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getBiEntityCondition().validate(reporter.makeChild(".bientity_condition"));
		getRenderArmorProvider().validate(reporter.makeChild(".render_armor"));
		getRenderOutlineProvider().validate(reporter.makeChild(".render_outline"));

	}

	public static class Impl extends Power.Impl<ModifyInvisibilityPower> {

		protected Impl(@NotNull Entity holder, @NotNull ModifyInvisibilityPower power) {
			super(holder, power);
		}

		public boolean isInvisibleTo(Context context) {
			context = this.addPowerContext(context);
			return this.isActive(context)
				&& power.getBiEntityCondition().test(context.makeChild(".bientity_condition"));
		}

		public boolean shouldRenderArmor(Context context) {
			context = this.addPowerContext(context);
			return this.isActive(context)
				&& power.getRenderArmorProvider().next(context.makeChild(".render_armor"));
		}

		public boolean shouldRenderOutline(Context context) {
			context = this.addPowerContext(context);
			return this.isActive(context)
				&& power.getRenderOutlineProvider().next(context.makeChild(".render_outline"));
		}

	}

	public static Context createContext(@NotNull Entity target, @Nullable Entity viewer) {
		return PowerTypes.MODIFY_INVISIBILITY.contextBuilder()
			.addNullable(ContextParameters.ACTOR, viewer)
			.add(ContextParameters.TARGET, target)
			.add(ContextParameters.ENTITY, target)
			.add(ContextParameters.ENTITY_POS, target.getPos())
			.build(target.getWorld());
	}

}
