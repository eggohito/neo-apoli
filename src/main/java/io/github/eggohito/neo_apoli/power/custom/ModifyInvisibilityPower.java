package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

public class ModifyInvisibilityPower extends Power {

	public static final MapCodec<ModifyInvisibilityPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(BiEntityCondition.CODEC.optionalFieldOf("bientity_condition", new ConstantBiEntityCondition(true)).forGetter(ModifyInvisibilityPower::getBiEntityCondition))
		.and(Codec.BOOL.optionalFieldOf("render_armor", true).forGetter(ModifyInvisibilityPower::shouldRenderArmor))
		.and(Codec.BOOL.optionalFieldOf("render_outline", true).forGetter(ModifyInvisibilityPower::shouldRenderOutline))
		.apply(instance, ModifyInvisibilityPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyInvisibilityPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			BiEntityCondition.PACKET_CODEC.encode(buf, power.getBiEntityCondition());
			buf.writeBoolean(power.shouldRenderArmor());
			buf.writeBoolean(power.shouldRenderOutline());
		},
		(buf, properties, condition) -> new ModifyInvisibilityPower(properties, condition,
			BiEntityCondition.PACKET_CODEC.decode(buf),
			buf.readBoolean(),
			buf.readBoolean()
		)
	);

	@Getter
	private final BiEntityCondition biEntityCondition;

	private final boolean renderArmor;
	private final boolean renderOutline;

	public ModifyInvisibilityPower(Properties properties, EntityCondition activeCondition, BiEntityCondition biEntityCondition, boolean renderArmor, boolean renderOutline) {
		super(properties, activeCondition);
		this.biEntityCondition = biEntityCondition;
		this.renderArmor = renderArmor;
		this.renderOutline = renderOutline;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_INVISIBILITY;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	public boolean shouldRenderArmor() {
		return renderArmor;
	}

	public boolean shouldRenderOutline() {
		return renderOutline;
	}

	public static class Impl extends Power.Impl<ModifyInvisibilityPower> {

		protected Impl(@NotNull Entity holder, @NotNull ModifyInvisibilityPower power) {
			super(holder, power);
		}

		public boolean isInvisibleTo(Entity viewer) {

			Context context = this.contextBuilder()
				.add(ContextParameters.ACTOR, viewer)
				.add(ContextParameters.TARGET, holder)
				.build(holder.getWorld());

			return power.getBiEntityCondition().test(context.makeChild(".bientity_condition"))
				&& this.isActive(context);

		}

		public boolean shouldRenderArmor() {
			return power.shouldRenderArmor();
		}

		public boolean shouldRenderOutline() {
			return power.shouldRenderOutline();
		}

		public boolean isActive() {
			return this.isActive(this.genericContext());
		}

	}

	public static boolean isInvisibleTo(@NotNull Entity target, @NotNull Entity actor) {
		return PowersComponent.hasPowerImpl(target, Impl.class, impl -> impl.isInvisibleTo(actor));
	}

}
