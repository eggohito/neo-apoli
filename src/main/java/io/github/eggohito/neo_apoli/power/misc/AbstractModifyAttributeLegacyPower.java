package io.github.eggohito.neo_apoli.power.misc;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
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
import java.util.Optional;
import java.util.function.BiConsumer;

@Getter
public abstract class AbstractModifyAttributeLegacyPower extends Power {

	private final List<AttributeModifier> modifiers;
	private final BooleanProvider sendUpdate;

	public AbstractModifyAttributeLegacyPower(Properties properties, Optional<EntityCondition> activeCondition, List<AttributeModifier> modifiers, BooleanProvider sendUpdate) {
		super(properties, activeCondition);
		this.modifiers = modifiers;
		this.sendUpdate = sendUpdate;
	}

	public AbstractModifyAttributeLegacyPower(Properties properties, List<AttributeModifier> modifiers, BooleanProvider sendUpdate) {
		super(properties);
		this.modifiers = modifiers;
		this.sendUpdate = sendUpdate;
	}

	@Override
	public abstract AbstractModifyAttributeLegacyPower.Impl<?> createImpl(Entity holder);

	public static abstract class Impl<P extends AbstractModifyAttributeLegacyPower> extends Power.Impl<P> {

		protected Impl(@NotNull Entity holder, @NotNull P power) {
			super(holder, power);
		}

		protected void processModifiers(Context context, BiConsumer<EntityAttributeInstance, EntityAttributeModifier> processor) {

			if (!(holder instanceof LivingEntity livingHolder) || livingHolder.getWorld().isClient()) {
				return;
			}

			Context sendUpdateContext = context.makeChild(".send_update");
			boolean sendUpdate = power.getSendUpdate().next(sendUpdateContext);

			if (!sendUpdateContext.hasErrors()) {

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

		}

		protected void addModifiersPersistently(Context context) {
			this.processModifiers(context, EntityAttributeInstance::overwritePersistentModifier);
		}

		protected void addModifiersTemporarily(Context context) {
			this.processModifiers(context, (attributeInstance, modifier) -> {

				if (!attributeInstance.hasModifier(modifier.id())) {
					attributeInstance.addTemporaryModifier(modifier);
				}

			});
		}

		protected void removeModifiers(Context context) {
			this.processModifiers(context, EntityAttributeInstance::removeModifier);
		}

	}

	protected static <P extends AbstractModifyAttributeLegacyPower> Products.P3<RecordCodecBuilder.Mu<P>, Properties, List<AttributeModifier>, BooleanProvider> addAttributeModifyingFields(RecordCodecBuilder.Instance<P> instance) {
		return Power.addCommonFields(instance)
			.and(NeoApoliCodecs.NONEMPTY_ATTRIBUTE_MODIFIERS.fieldOf("modifiers").forGetter(AbstractModifyAttributeLegacyPower::getModifiers))
			.and(BooleanProvider.CODEC.optionalFieldOf("send_update", new ConstantBooleanProvider(true)).forGetter(AbstractModifyAttributeLegacyPower::getSendUpdate));
	}

	protected static <P extends AbstractModifyAttributeLegacyPower> Products.P4<RecordCodecBuilder.Mu<P>, Properties, Optional<EntityCondition>, List<AttributeModifier>, BooleanProvider> addAttributeModifyingAndConditionFields(RecordCodecBuilder.Instance<P> instance) {
		return Power.addCommonConditionedFields(instance)
			.and(NeoApoliCodecs.NONEMPTY_ATTRIBUTE_MODIFIERS.fieldOf("modifiers").forGetter(AbstractModifyAttributeLegacyPower::getModifiers))
			.and(BooleanProvider.CODEC.optionalFieldOf("send_update", new ConstantBooleanProvider(true)).forGetter(AbstractModifyAttributeLegacyPower::getSendUpdate));
	}

	protected static <P extends AbstractModifyAttributeLegacyPower> PacketCodec<RegistryByteBuf, P> createAttributeModifyingPacketCodec(BiConsumer<RegistryByteBuf, P> encoder, Function4<RegistryByteBuf, Properties, List<AttributeModifier>, BooleanProvider, P> decoder) {
		return Power.createCommonPacketCodec(
			(buf, power) -> {
				NeoApoliPacketCodecs.ATTRIBUTE_MODIFIERS.encode(buf, power.getModifiers());
				BooleanProvider.PACKET_CODEC.encode(buf, power.getSendUpdate());
				encoder.accept(buf, power);
			},
			(buf, properties) -> {
				List<AttributeModifier> modifiers = NeoApoliPacketCodecs.ATTRIBUTE_MODIFIERS.decode(buf);
				BooleanProvider sendUpdate = BooleanProvider.PACKET_CODEC.decode(buf);
				return decoder.apply(buf, properties, modifiers, sendUpdate);
			}
		);
	}

	protected static <P extends AbstractModifyAttributeLegacyPower> PacketCodec<RegistryByteBuf, P> createAttributeModifyingConditionedPacketCodec(BiConsumer<RegistryByteBuf, P> encoder, Function5<RegistryByteBuf, Properties, Optional<EntityCondition>, List<AttributeModifier>, BooleanProvider, P> decoder) {
		return Power.createCommonConditionedPacketCodec(
			(buf, power) -> {
				NeoApoliPacketCodecs.ATTRIBUTE_MODIFIERS.encode(buf, power.getModifiers());
				BooleanProvider.PACKET_CODEC.encode(buf, power.getSendUpdate());
				encoder.accept(buf, power);
			},
			(buf, properties, activeCondition) -> {
				List<AttributeModifier> modifiers = NeoApoliPacketCodecs.ATTRIBUTE_MODIFIERS.decode(buf);
				BooleanProvider sendUpdate = BooleanProvider.PACKET_CODEC.decode(buf);
				return decoder.apply(buf, properties, activeCondition, modifiers, sendUpdate);
			}
		);
	}

}
