package io.github.eggohito.neo_apoli.keybinding;

import java.util.Objects;
import net.minecraft.util.Mth;

public record KeyState(String id, long pressedTime) {

	public KeyState {
		pressedTime = Mth.clamp(-1, pressedTime, Long.MAX_VALUE);
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		else if (obj instanceof KeyState that) {
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
