package io.github.eggohito.neo_apoli.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.util.ProblemReporter;
import org.apache.commons.lang3.function.Consumers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Reporter implements ProblemReporter {

	public static Reporter NO_OP = new Reporter() {

		@Override
		public @NotNull Reporter forChild(String name) {
			return this;
		}

		@Override
		public void report(String message) {

		}

	};

	@Nullable
	private final Reporter parent;
	private final List<Reporter> children;

	private final Set<String> problems;
	private final int depth;

	private final String fullPath;
	private final String path;

	protected Reporter(Reporter parent, String path) {
		this.parent = parent;
		this.children = new ObjectArrayList<>();
		this.problems = new ObjectLinkedOpenHashSet<>();
		this.depth = parent.depth + 1;
		this.fullPath = parent.fullPath + path;
		this.path = path;
	}

	public Reporter(String path) {
		this.parent = null;
		this.children = new ObjectArrayList<>();
		this.problems = new ObjectLinkedOpenHashSet<>();
		this.fullPath = path;
		this.path = path;
		this.depth = 0;
	}

	public Reporter() {
		this("");
	}

	@Override
	public @NotNull Reporter forChild(String name) {

		var child = new Reporter(this, name);
		this.children.add(child);

		return child;

	}

	@Override
	public void report(String message) {
		this.problems.add(message);
	}

	public Reporter getRoot() {

		Reporter root = this;

		while (root.parent != null) {
			root = root.parent;
		}

		return root;

	}

	public List<String> getTreeReport() {

		List<String> lines = new ObjectArrayList<>();
		boolean oneLined = false, prependPath = false;

		if (this.problems.size() == 1 && !this.path.isEmpty()) {
			oneLined = lines.add(this.indent(this.getPathForTree() + ": " + this.problems.iterator().next()));
		}

		else {

			for (var problem : this.problems) {
				lines.add(this.indent("    * " + problem));
			}

		}

		for (var child : this.children) {
			prependPath |= lines.addAll(child.getTreeReport());
		}

		if (!oneLined && prependPath && !this.path.isEmpty()) {
			lines.addFirst(this.indent(this.getPathForTree() + ": "));
		}

		return lines;

	}

	public String getReport() {
		return String.join("\n", this.getTreeReport());
	}

	public boolean hasProblems() {

		if (!this.problems.isEmpty()) {
			return true;
		}

		for (var child : children) {

			if (child.hasProblems()) {
				return true;
			}

		}

		return false;

	}

	protected String indent(String line) {
		return "  ".repeat(this.depth) + line;
	}

	protected String getPathForTree() {
		return this.depth > 0 ? "|-" + this.path : this.path;
	}

	public static class Scoped extends Reporter implements AutoCloseable {

		public static final Scoped NO_OP = new Scoped(Consumers.nop()) {

			@Override
			public @NotNull Reporter forChild(String name) {
				return this;
			}

			@Override
			public void report(String message) {

			}

		};

		private final Consumer<String> handler;

		public Scoped(String path, Consumer<String> handler) {
			super(path);
			this.handler = handler;
		}

		public Scoped(Consumer<String> handler) {
			this.handler = handler;
		}

		@Override
		public void close() {

			if (this.hasProblems()) {
				handler.accept(this.getReport());
			}

		}

	}

}
