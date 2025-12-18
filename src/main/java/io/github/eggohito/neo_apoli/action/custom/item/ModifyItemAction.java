package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record ModifyItemAction(TypedContextKey<Entity> entity, ResourceKey<LootItemFunction> modifier) implements ItemAction {

	public static final MapCodec<ModifyItemAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.ENTITY_CONTEXT_KEY.optionalFieldOf("entity", NeoApoliContextKeys.THIS_ENTITY).forGetter(ModifyItemAction::entity),
		CodecUtil.resourceKey(Registries.ITEM_MODIFIER).fieldOf("modifier").forGetter(ModifyItemAction::modifier)
	).apply(instance, ModifyItemAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyItemAction> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, ModifyItemAction::entity,
		ResourceKey.streamCodec(Registries.ITEM_MODIFIER), ModifyItemAction::modifier,
		ModifyItemAction::new
	);

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.MODIFY;
	}

	@Override
	public void execute(Context context) {

		if (!(context.getLevel() instanceof ServerLevel serverLevel) || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		SlotAccess stackReference = context.required(NeoApoliContextKeys.STACK_REFERENCE);
		ItemStack stack = stackReference.get();

		LootItemFunction modifier = serverLevel.getServer().reloadableRegistries().lookup()
			.getOrThrow(this.modifier())
			.value();

		LootParams lootParams = new LootParams.Builder(serverLevel)
			.withParameter(LootContextParams.ORIGIN, context.optional(NeoApoliContextKeys.THIS_POS).orElse(Vec3.ZERO))
			.withOptionalParameter(LootContextParams.THIS_ENTITY, context.nullable(entity()))
			.create(LootContextParamSets.COMMAND);

		LootContext lootContext = new LootContext.Builder(lootParams).create(Optional.empty());
		lootContext.pushVisitedElement(LootContext.createVisitedEntry(modifier));

		ItemStack newStack = modifier.apply(stack.copy(), lootContext);
		newStack.limitSize(newStack.getMaxStackSize());

		stackReference.set(newStack);

	}

	@Override
	public void validate(Context.Validator validator) {
		ItemAction.super.validate(validator);
		RegistryUtil.validateEntry(validator, this.modifier());
	}

}
