BUILD_DIR := build

default: clox

# Remove all build outputs and intermediate files.
clean:
	@ rm -rf $(BUILD_DIR)
	@ rm clox

# TODO: figure out testing structure.
# Run the tests for clox against a debug version of clox.
test: debug
	@ echo "TODO: run some tests..."

# Compile a debug build of clox.
debug:
	@ $(MAKE) -f util/c.make NAME=cloxd MODE=debug SOURCE_DIR=src

# Compile a release version of the C interpreter.
clox:
	@ $(MAKE) -f util/c.make NAME=clox MODE=release SOURCE_DIR=src
	@ cp build/clox clox # For convenience, copy the interpreter to the top level.
