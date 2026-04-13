################################################################################
# Makefile para compilar programas en java
################################################################################

JC	=	javac

# Esta es la forma de GNU make
#%.class : %.java
#	$(JC) $(JAVAFLAGS) $<

CLASSES = Assembler.class

.SUFFIXES: .class .java
.java.class:
	$(JC) $(JAVAFLAGS) $<

% : %.java
	$(JC) $(JAVAFLAGS) $<

all: $(CLASSES)

clean:
	rm -f *.class

Assembler.class: Assembler.java

################################################################################
