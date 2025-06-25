package io.github.eggohito.neo_apoli.condition;

import net.minecraft.util.Identifier;

import java.util.Objects;

public record ConditionEntry<C extends Condition>(Identifier id, C value) {

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id());
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		else if (obj instanceof ConditionEntry<?> that) {
			return Objects.equals(this.id(), that.id());
		}

		else {
			return false;
		}

	}

}
