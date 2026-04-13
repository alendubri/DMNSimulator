################################################################################
# Makefile para compilar programas en java
################################################################################

JC	=	javac

# Esta es la forma de GNU make
#%.class : %.java
#	$(JC) $(JAVAFLAGS) $<

CLASSES = Memory.class DataMemory.class InstMemory.class \
          RegFileMemory.class

.SUFFIXES: .class .java
.java.class:
	$(JC) $(JAVAFLAGS) $<

% : %.java
	$(JC) $(JAVAFLAGS) $<

all: $(CLASSES)

clean:
	rm -f *.class

Memory.class: Memory.java
DataMemory.class: Memory.class DataMemory.java
InstMemory.class: Memory.class InstMemory.java
RegFileMemory.class: Memory.class RegFileMemory.java

################################################################################
