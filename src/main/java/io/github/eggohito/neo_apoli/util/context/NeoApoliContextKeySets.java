package io.github.eggohito.neo_apoli.util.context;

import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.Util;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.Entity;

public class NeoApoliContextKeySets {

	public static final ContextKeySet ANY = Util.make(new ContextKeySet.Builder(), builder -> {

		for (var parameter : NeoApoliRegistries.TYPED_CONTEXT_KEY) {
			builder.optional(parameter);
		}

	}).build();

	public static final ContextKeySet GENERIC = new ContextKeySet.Builder()
		.optional(NeoApoliContextKeys.HAND)
		.optional(NeoApoliContextKeys.POWER_REFERENCE)
		.build();

	public static final ContextKeySet BIENTITY = new ContextKeySet.Builder()
		.optional(NeoApoliContextKeys.ACTOR_ENTITY)
		.optional(NeoApoliContextKeys.TARGET_ENTITY)
		.build();

	public static final ContextKeySet BLOCK = new ContextKeySet.Builder()
		.required(NeoApoliContextKeys.BLOCK_POS)
		.required(NeoApoliContextKeys.BLOCK_STATE)
		.optional(NeoApoliContextKeys.BLOCK_ENTITY)
		.optional(NeoApoliContextKeys.DIRECTION)
		.build();

	public static final ContextKeySet DAMAGE = new ContextKeySet.Builder()
		.required(NeoApoliContextKeys.DAMAGE_SOURCE)
		.optional(NeoApoliContextKeys.DAMAGE_AMOUNT)
		.optional(NeoApoliContextKeys.DAMAGING_ENTITY)
		.optional(NeoApoliContextKeys.DIRECT_DAMAGING_ENTITY)
		.build();

	public static final ContextKeySet DAMAGE_WITHOUT_AMOUNT = new ContextKeySet.Builder()
		.required(NeoApoliContextKeys.DAMAGE_SOURCE)
		.optional(NeoApoliContextKeys.DAMAGING_ENTITY)
		.optional(NeoApoliContextKeys.DIRECT_DAMAGING_ENTITY)
		.build();

	public static final ContextKeySet ENTITY = Util.make(new ContextKeySet.Builder(), builder -> {

		for (var parameter : NeoApoliRegistries.TYPED_CONTEXT_KEY) {

			if (Entity.class.isAssignableFrom(parameter.getTypeClass())) {
				builder.optional(parameter);
			}

		}

		builder.required(NeoApoliContextKeys.THIS_POS);

	}).build();

	public static final ContextKeySet ITEM = new ContextKeySet.Builder()
		.optional(NeoApoliContextKeys.STACK_REFERENCE)
		.optional(NeoApoliContextKeys.ITEM_STACK)
		.build();

	public static final ContextKeySet NUMBER_BOUND = new ContextKeySet.Builder()
		.optional(NeoApoliContextKeys.CURRENT_VALUE)
		.optional(NeoApoliContextKeys.MIN_VALUE)
		.optional(NeoApoliContextKeys.MAX_VALUE)
		.build();

	public static void init() {

	}

}
