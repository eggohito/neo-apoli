package io.github.eggohito.neo_apoli.util.context;

import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextType;

public class NeoApoliContextTypes {

	public static final ContextType ANY = Util.make(new ContextType.Builder(), builder -> {

		for (var parameter : NeoApoliRegistries.TYPED_CONTEXT_PARAMETER) {
			builder.allow(parameter);
		}

	}).build();

	public static final ContextType GENERIC = new ContextType.Builder()
		.allow(NeoApoliContextParameters.POWER_REFERENCE)
		.allow(NeoApoliContextParameters.HAND)
		.build();

	public static final ContextType BIENTITY = new ContextType.Builder()
		.allow(NeoApoliContextParameters.ACTOR)
		.allow(NeoApoliContextParameters.TARGET)
		.build();

	public static final ContextType BLOCK = new ContextType.Builder()
		.require(NeoApoliContextParameters.BLOCK_POS)
		.require(NeoApoliContextParameters.BLOCK_STATE)
		.allow(NeoApoliContextParameters.BLOCK_ENTITY)
		.allow(NeoApoliContextParameters.DIRECTION)
		.build();

	public static final ContextType DAMAGE = new ContextType.Builder()
		.require(NeoApoliContextParameters.DAMAGE_SOURCE)
		.allow(NeoApoliContextParameters.DAMAGE_AMOUNT)
		.allow(NeoApoliContextParameters.DAMAGING_ENTITY)
		.allow(NeoApoliContextParameters.DIRECT_DAMAGING_ENTITY)
		.build();

	public static final ContextType ENTITY = Util.make(new ContextType.Builder(), builder -> {

		for (var parameter : NeoApoliRegistries.TYPED_CONTEXT_PARAMETER) {

			if (parameter.getTypeClass().isAssignableFrom(Entity.class)) {
				builder.allow(parameter);
			}

		}

		builder.require(NeoApoliContextParameters.ENTITY_POS);

	}).build();

	public static final ContextType ITEM = new ContextType.Builder()
		.allow(NeoApoliContextParameters.STACK_REFERENCE)
		.allow(NeoApoliContextParameters.ITEM_STACK)
		.build();

	public static void init() {

	}

}
