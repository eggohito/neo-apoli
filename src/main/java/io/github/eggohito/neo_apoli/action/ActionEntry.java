package io.github.eggohito.neo_apoli.action;

import net.minecraft.util.Identifier;

import java.util.Objects;

public record ActionEntry<A extends Action>(Identifier id, A value) {

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id());
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		else if (obj instanceof ActionEntry<?> that) {
			return Objects.equals(this.id(), that.id());
		}

		else {
			return false;
		}

	}

}
