package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.item.ConstantItemCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

@Getter
public class PreventItemUsePower extends Power {

	public static final MapCodec<PreventItemUsePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(ItemCondition.CODEC.optionalFieldOf("item_condition", new ConstantItemCondition(true)).forGetter(PreventItemUsePower::getItemCondition))
		.and(NeoApoliCodecs.HAND_SET.optionalFieldOf("hands", EnumSet.allOf(Hand.class)).forGetter(PreventItemUsePower::getHands))
		.apply(instance, PreventItemUsePower::new));

	public static final PacketCodec<RegistryByteBuf, PreventItemUsePower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			ItemCondition.PACKET_CODEC.encode(buf, power.getItemCondition());
			NeoApoliPacketCodecs.HAND_SET.encode(buf, power.getHands());
		},
		(buf, properties, activeCondition) -> new PreventItemUsePower(properties, activeCondition,
			ItemCondition.PACKET_CODEC.decode(buf),
			NeoApoliPacketCodecs.HAND_SET.decode(buf)
		)
	);

	private final ItemCondition itemCondition;
	private final EnumSet<Hand> hands;

	public PreventItemUsePower(Properties properties, EntityCondition activeCondition, ItemCondition itemCondition, EnumSet<Hand> hands) {
		super(properties, activeCondition);
		this.itemCondition = itemCondition;
		this.hands = hands;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.PREVENT_ITEM_USE;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	public static class Impl extends Power.Impl<PreventItemUsePower> {

		protected Impl(@NotNull Entity holder, @NotNull PreventItemUsePower power) {
			super(holder, power);
		}

		public boolean doesApply(Context context) {
			context = this.addPowerContext(context);
			return power.getHands().contains(context.required(ContextParameters.HAND))
				&& this.isActive(context)
				&& power.getItemCondition().test(context);
		}

	}

	public static Context createContext(World world, PlayerEntity user, Hand hand, ItemStack stack) {
		return PowerTypes.PREVENT_ITEM_USE.contextBuilder()
			.add(ContextParameters.HAND, hand)
			.add(ContextParameters.ENTITY, user)
			.add(ContextParameters.ENTITY_POS, user.getPos())
			.add(ContextParameters.ITEM_STACK, stack)
			.build(world);
	}

}
