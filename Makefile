################################################################################
# Unified Makefile for the modern JDK build.
# Keeps the historical entry points, but compiles the whole source tree from the
# repository root into a dedicated build directory.
################################################################################

JAVAC ?= javac
JAVA ?= java
JAR ?= jar

BUILD_DIR ?= build
CLASSES_DIR := $(BUILD_DIR)/classes
MAIN_CLASS := SimDMN

JAVA_SOURCES := $(shell find . -path './RCS' -prune -o -path '*/RCS' -prune -o -name '*.java' -print)

.PHONY: all compile exec run clean appletexec sources legacy-clean

all: compile

compile: $(CLASSES_DIR)/.compiled

$(CLASSES_DIR)/.compiled: $(JAVA_SOURCES)
	mkdir -p $(CLASSES_DIR)
	$(JAVAC) -d $(CLASSES_DIR) $(JAVA_SOURCES)
	touch $(CLASSES_DIR)/.compiled

run exec: compile
	$(JAVA) -cp $(CLASSES_DIR) $(MAIN_CLASS)

appletexec: compile
	@printf '%s\n' 'Applet execution is not supported by the current JDK toolchain.'
	@printf '%s\n' 'The legacy applet sources still compile, but modern OpenJDK no longer provides appletviewer.'

clean:
	rm -rf $(BUILD_DIR)

sources:
	@printf '%s\n' $(JAVA_SOURCES)

legacy-clean:
	find . -path './RCS' -prune -o -path '*/RCS' -prune -o -name '*.class' -print -delete
