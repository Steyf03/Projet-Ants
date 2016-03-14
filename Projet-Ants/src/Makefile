JAVAC=javac
JAVAC_ARGS=

SOURCES=Ant.java Connection.java Direction.java Logger.java MyBot.java Strategy.java Tile.java Type.java
CLASSES=$(addsuffix .class, $(basename ${SOURCES}))
JAR=MyBot.jar

.PHONY: all clean

all: $(CLASSES) $(JAR)

$(JAR): $(CLASSES)
	jar cvfm $(JAR) Manifest.txt *.class

%.class: %.java
	$(JAVAC) $(JAVAC_ARGS) $<

clean:
	-rm -Rf $(CLASSES)
	-rm -Rf *.class
	-rm -Rf $(JAR)