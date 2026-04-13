################################################################################
# Makefile para compilar programas en java
################################################################################

JC	=	javac

CLASSES = Word.class Byte.class Nibble.class \
          BinQueue.class ByteQueue.class NibbleQueue.class WordQueue.class \
			 BinConvert.class

# JAVAFLAGS = -classpath .:..:${CLASSPATH}

# Esta es la forma de GNU make
#%.class : %.java
#	$(JC) $(JAVAFLAGS) $<

.SUFFIXES: .class .java
.java.class:
	$(JC) $(JAVAFLAGS) $<

% : %.java
	$(JC) $(JAVAFLAGS) $<

all: $(CLASSES)

clean:
	rm -f *.class

Word.class: Word.java

Byte.class: Word.class Byte.java

Nibble.class: Word.class Nibble.java

BinQueue.class: Word.class BinQueue.java

ByteQueue.class: BinQueue.class Byte.class ByteQueue.java

NibbleQueue.class: BinQueue.class Nibble.class NibbleQueue.java

WordQueue.class: BinQueue.class Word.class WordQueue.java

BinConvert.class: Word.class Byte.class Nibble.class BinConvert.java

################################################################################
