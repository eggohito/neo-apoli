package io.github.eggohito.neo_apoli.context;

/**
 *  An interface injected to {@link net.minecraft.commands.CommandSourceStack} to be able to store and access
 *  a {@link Context.Builder} to be used in {@link io.github.eggohito.neo_apoli.command.ActionCommand /action} and
 *  {@link io.github.eggohito.neo_apoli.command.ConditionCommand /condition} commands.
 */
public interface ContextBuilderHolder {

	default Context.Builder neo_apoli$getContextBuilder() {
		throw new AssertionError("Implemented via mixin");
	}

}
