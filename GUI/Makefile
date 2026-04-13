#$Id: Makefile,v 1.5 1996/11/13 13:19:42 dubuc Exp $
################################################################################
# Makefile para compilar programas en java
################################################################################

#
#$Log: Makefile,v $
#Revision 1.5  1996/11/13 13:19:42  dubuc
#Finalizacion de proyecto en esta revision, se igualan todas las revisiones
#revision 1.5
#
#

JC	=	javac

# Esta es la forma de GNU make
#%.class : %.java
#	$(JC) $(JAVAFLAGS) $<

CLASSES = DMNFrame.class MainMenu.class RegisterPanel.class Arrow.class \
          DMNFigPanel.class PCQPanel.class QueuePanel.class \
			 RegisterList.class DataMemList.class InstMemList.class \
			 ClockCanvas.class EditorFrame.class StagesPanel.class

.SUFFIXES: .class .java
.java.class:
	$(JC) $(JAVAFLAGS) $<

% : %.java
	$(JC) $(JAVAFLAGS) $<

all: $(CLASSES)

clean:
	rm -f *.class

DMNFrame.class: MainMenu.class QueuePanel.class PCQPanel.class \
                DataMemList.class RegisterList.class DMNFigPanel.class \
					 ClockCanvas.class DMNFrame.java

MainMenu.class: MainMenu.java

DMNFigPanel.class: Arrow.class DMNFigPanel.java

Arrow.class: Arrow.java

PCQPanel.class: PCQPanel.java

QueuePanel.class: QueuePanel.java

RegisterList.class: RegisterList.java

DataMemList.class: DataMemList.java

InstMemList.class: InstMemList.java

ClockCanvas.class: ClockCanvas.java

EditorFrame.class: EditorFrame.java

StagesPanel.class: StagesPanel.java

################################################################################
