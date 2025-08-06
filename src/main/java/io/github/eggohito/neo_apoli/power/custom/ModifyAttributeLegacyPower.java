package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.meta.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.AttributeModifier;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

@Getter
public class ModifyAttributeLegacyPower extends Power {

	public static final MapCodec<ModifyAttributeLegacyPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance)
		.and(NeoApoliCodecs.NONEMPTY_ATTRIBUTE_MODIFIERS.fieldOf("modifiers").forGetter(ModifyAttributeLegacyPower::getModifiers))
		.and(BooleanProvider.CODEC.optionalFieldOf("send_update", new ConstantBooleanProvider(true)).forGetter(ModifyAttributeLegacyPower::getSendUpdateProvider))
		.apply(instance, ModifyAttributeLegacyPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyAttributeLegacyPower> PACKET_CODEC = createCommonPacketCodec(
		(buf, power) -> {
			NeoApoliPacketCodecs.ATTRIBUTE_MODIFIERS.encode(buf, power.getModifiers());
			BooleanProvider.PACKET_CODEC.encode(buf, power.getSendUpdateProvider());
		},
		(buf, properties) -> new ModifyAttributeLegacyPower(properties,
			NeoApoliPacketCodecs.ATTRIBUTE_MODIFIERS.decode(buf),
			BooleanProvider.PACKET_CODEC.decode(buf)
		)
	);

	private final List<AttributeModifier> modifiers;
	private final BooleanProvider sendUpdateProvider;

	public ModifyAttributeLegacyPower(Properties properties, List<AttributeModifier> modifiers, BooleanProvider sendUpdateProvider) {
		super(properties);
		this.modifiers = modifiers;
		this.sendUpdateProvider = sendUpdateProvider;
	}

	public ModifyAttributeLegacyPower(Properties properties, EntityCondition activeCondition, List<AttributeModifier> modifiers, BooleanProvider sendUpdateProvider) {
		super(properties, activeCondition);
		this.modifiers = modifiers;
		this.sendUpdateProvider = sendUpdateProvider;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_ATTRIBUTE_LEGACY;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	public static class Impl extends Power.Impl<ModifyAttributeLegacyPower> {

		protected Impl(@NotNull Entity holder, @NotNull ModifyAttributeLegacyPower power) {
			super(holder, power);
		}

		@Override
		public void onGranted() {
			Context context = this.createGenericContext();
			this.addModifiers(context);
		}

		@Override
		public void onRespawn() {
			Context context = this.createGenericContext();
			this.addModifiers(context);	//	Re-apply attribute modifiers because they do not persist through respawn
		}

		@Override
		public void onRevoked() {
			Context context = this.createGenericContext();
			this.removeModifiers(context);
		}

		protected void processModifiers(Context context, BiConsumer<EntityAttributeInstance, EntityAttributeModifier> processor) {

			if (holder.getWorld().isClient() || !(holder instanceof LivingEntity livingHolder)) {
				return;
			}

			Context sendUpdateContext = context.makeChild(".send_update");
			boolean sendUpdate = power.getSendUpdateProvider().next(sendUpdateContext);

			if (sendUpdateContext.hasErrors()) {
				return;
			}

			for (var attributeModifier: power.getModifiers()) {

				EntityAttributeInstance attributeInstance = livingHolder.getAttributeInstance(attributeModifier.attribute());
				EntityAttributeModifier modifier = attributeModifier.modifier();

				if (attributeInstance == null) {
					continue;
				}

				float prevMaxHealth = livingHolder.getMaxHealth();
				float prevMaxHealthPercent = livingHolder.getHealth() / prevMaxHealth;

				processor.accept(attributeInstance, modifier);
				float currMaxHealth = livingHolder.getMaxHealth();

				if (sendUpdate && prevMaxHealth != currMaxHealth) {
					livingHolder.setHealth(currMaxHealth * prevMaxHealthPercent);
				}

			}

		}

		protected void addModifiers(Context context) {
			this.processModifiers(context, EntityAttributeInstance::overwritePersistentModifier);
		}

		protected void removeModifiers(Context context) {
			this.processModifiers(context, EntityAttributeInstance::removeModifier);
		}

	}

}
