package io.github.eggohito.neo_apoli.action.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionType;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.provider.custom.vec3.ConstantVec3Provider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
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

import java.util.Optional;

public record ModifyItemAction(ResourceKey<LootItemFunction> modifier, ContextParameter<Entity> entity, Vec3Provider pos) implements ItemAction {

	public static final MapCodec<ModifyItemAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceKey.codec(Registries.ITEM_MODIFIER).fieldOf("modifier").forGetter(ModifyItemAction::modifier),
		NeoApoliCodecs.ENTITY_CONTEXT_PARAM.optionalFieldOf("entity", NeoApoliContextParams.THIS_ENTITY).forGetter(ModifyItemAction::entity),
		Vec3Provider.CODEC.optionalFieldOf("pos", new ConstantVec3Provider(0, 0, 0)).forGetter(ModifyItemAction::pos)
	).apply(instance, ModifyItemAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyItemAction> STREAM_CODEC = StreamCodec.composite(
		ResourceKey.streamCodec(Registries.ITEM_MODIFIER), ModifyItemAction::modifier,
		NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, ModifyItemAction::entity,
		Vec3Provider.STREAM_CODEC, ModifyItemAction::pos,
		ModifyItemAction::new
	);

	@Override
	public ItemActionType<?> getType() {
		return ItemActionTypes.MODIFY;
	}

	@Override
	public void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel) || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		SlotAccess slotAccess = context.getRequired(NeoApoliContextParams.SLOT_ACCESS);
		ItemStack stack = slotAccess.get();

		LootItemFunction modifier = serverLevel.getServer().reloadableRegistries().lookup()
			.getOrThrow(this.modifier())
			.value();
		LootParams lootParams = new LootParams.Builder(serverLevel)
			.withOptionalParameter(LootContextParams.THIS_ENTITY, context.getNullable(entity()))
			.withParameter(LootContextParams.ORIGIN, pos().next(context.forChild(".pos")))
			.create(LootContextParamSets.COMMAND);

		LootContext lootContext = new LootContext.Builder(lootParams).create(Optional.empty());
		lootContext.pushVisitedElement(LootContext.createVisitedEntry(modifier));

		ItemStack newStack = modifier.apply(stack.copy(), lootContext);
		newStack.limitSize(newStack.getMaxStackSize());

		slotAccess.set(newStack);

	}

	@Override
	public void validate(Context.Validator validator) {
		ItemAction.super.validate(validator);
		RegistryUtil.validateKey(validator, this.modifier());
	}

}
