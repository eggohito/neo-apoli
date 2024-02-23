package io.github.eggohito.neo_apoli;

import dev.greenhouseteam.rdpr.api.IReloadableRegistryCreationHelper;
import dev.greenhouseteam.rdpr.api.entrypoint.ReloadableRegistryPlugin;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;

//  FIXME:  For some reason, even though it is being registered in RDPR, it doesn't work...
public class NeoApoliRDPRPlugin implements ReloadableRegistryPlugin {

    @Override
    public void createContents(IReloadableRegistryCreationHelper helper) {
        helper.fromExistingRegistry(NeoApoliRegistryKeys.POWERS);
    }

}
