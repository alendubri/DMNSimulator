################################################################################
# Compatibility shim: delegate package builds to the unified root Makefile.
################################################################################

.PHONY: all compile clean exec run appletexec

all compile clean exec run appletexec:
	$(MAKE) -C .. $@
