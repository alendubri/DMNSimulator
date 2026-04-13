################################################################################
# Makefile para compilar programas en java
################################################################################

JC	=	javac

# Esta es la forma de GNU make
#%.class : %.java
#	$(JC) $(JAVAFLAGS) $<

CLASSES = NMETransf.class ALLU.class DependResolver.class Simulator.class

.SUFFIXES: .class .java
.java.class:
	$(JC) $(JAVAFLAGS) $<

% : %.java
	$(JC) $(JAVAFLAGS) $<

all: binContainer memory $(CLASSES)

binContainer:
	(cd ../binContainer; make)

memory:
	(cd ../memory; make)

clean:
	(cd ../binContainer; make clean)
	(cd ../memory; make clean)
	rm -f *.class

ALLU.class: ALLU.java

DependResolver.class: DependResolver.java

Simulator.class: DependResolver.class ALLU.class Simulator.java

NMETransf.class: NMETransf.java

################################################################################
