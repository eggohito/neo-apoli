package io.github.eggohito.neo_apoli.keybinding;

import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public record KeyBindingState(String id, long pressedTime) {

	public KeyBindingState {
		pressedTime = MathHelper.clamp(-1, pressedTime, Long.MAX_VALUE);
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		else if (obj instanceof KeyBindingState that) {
			return Objects.equals(this.id(), that.id());
		}

		else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id());
	}

	public boolean pressed() {
		return pressedTime() > 0;
	}

}
