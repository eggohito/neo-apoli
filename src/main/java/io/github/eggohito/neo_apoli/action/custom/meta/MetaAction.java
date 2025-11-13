package io.github.eggohito.neo_apoli.action.custom.meta;

import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;

public interface MetaAction extends Action {

	@Override
	default String asDisplayString() {
		return "Meta action with type \"" + RegistryUtil.getId(NeoApoliRegistries.ACTION_TYPE, this.getType()) + "\"";
	}

}
