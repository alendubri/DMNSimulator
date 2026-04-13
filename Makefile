#$Id: Makefile,v 1.5.1.1 1996/11/13 13:47:59 dubuc Exp $
################################################################################
# Makefile para compilar programas en java
################################################################################

#
#$Log: Makefile,v $
#Revision 1.5.1.1  1996/11/13 13:47:59  dubuc
#Cambio en makefile para que limpie el directorio del Assembler
#
#Revision 1.5  1996/11/13 13:43:30  dubuc
#Finalizacion de proyecto en esta revision, se igualan todas las revisiones
#a la 1.5
#
#

JC	=	javac

# Esta es la forma de GNU make
#%.class : %.java
#	$(JC) $(JAVAFLAGS) $<

CLASSES = DMNApplet.class SimDMN.class

.SUFFIXES: .class .java
.java.class:
	$(JC) $(JAVAFLAGS) $<

% : %.java
	$(JC) $(JAVAFLAGS) $<

all: Simul.dir Assembler.dir GUI.dir $(CLASSES)

exec: all
	java SimDMN

appletexec: all
	appletviewer DMNApplet.html

GUI.dir:
	(cd GUI;make)

Simul.dir:
	(cd Simul;make)

Assembler.dir:
	(cd Assembler;make)

clean:
	rm -f *.class
	(cd Assembler;make clean)
	(cd Simul;make clean)
	(cd GUI;make clean)

clean-dir:
	rm -f *.class

DMNApplet.class: DMNApplet.java

SimDMN.class: SimDMN.java

################################################################################
