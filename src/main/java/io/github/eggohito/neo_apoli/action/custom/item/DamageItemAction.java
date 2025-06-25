package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.ItemAction;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.meta.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@EqualsAndHashCode(callSuper = false)
@Data
public final class DamageItemAction extends ItemAction {

	public static final MapCodec<DamageItemAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.optionalFieldOf("amount", new ConstantNumberProvider(1)).forGetter(DamageItemAction::amount),
		Codec.BOOL.optionalFieldOf("ignore_unbreaking", false).forGetter(DamageItemAction::ignoreUnbreaking)
	).apply(instance, DamageItemAction::new));

	public static final PacketCodec<RegistryByteBuf, DamageItemAction> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, DamageItemAction::amount,
		PacketCodecs.BOOLEAN, DamageItemAction::ignoreUnbreaking,
		DamageItemAction::new
	);

	private final NumberProvider amount;
	private final boolean ignoreUnbreaking;

	public DamageItemAction(NumberProvider amount, boolean ignoreUnbreaking) {
		this.amount = amount;
		this.ignoreUnbreaking = ignoreUnbreaking;
	}

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.DAMAGE;
	}

	@Override
	protected void impl(Context context) {

		if (!(context.getWorld() instanceof ServerWorld serverWorld)) {
			return;
		}

		Context amountContext = context.makeChild(".amount");
		int amount = this.amount().intValue(amountContext);

		if (amountContext.hasErrors()) {
			return;
		}

		ItemStack stack = context.required(ContextParameters.ITEM_STACK);
		ServerPlayerEntity serverPlayerHolder = context.optional(ContextParameters.THIS_ENTITY)
			.filter(ServerPlayerEntity.class::isInstance)
			.map(ServerPlayerEntity.class::cast)
			.orElse(null);

		if (this.ignoreUnbreaking()) {

			if (amount >= stack.getMaxCount()) {
				stack.decrement(1);
			}

			else {
				stack.setDamage(stack.getDamage() + amount);
			}

		}

		else {
			stack.damage(amount, serverWorld, serverPlayerHolder, item -> {});
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		amount().validate(reporter.makeChild(".amount"));
	}

}
