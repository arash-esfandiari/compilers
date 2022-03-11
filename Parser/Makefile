JAVA=java
JAVAC=javac
JFLEX=jflex
#CLASSPATH=-cp /usr/share/java/cup.jar:.
#CUP=cup
#JFLEX=~/Projects/jflex/bin/jflex
CLASSPATH=-cp ../java-cup-11b.jar:.
CUP=$(JAVA) $(CLASSPATH) java_cup.Main
# absyn/Decs/*.java absyn/Exps/*.java absyn/Lists/*.java absyn/Vars/*.java absyn/VarDecs/*.java

all: Main.class

Main.class: absyn/*.java  parser.java sym.java Lexer.java ShowTreeVisitor.java Scanner.java Main.java

%.class: %.java
	$(JAVAC) $(CLASSPATH) $^

Lexer.java: cminus.flex
	$(JFLEX) cminus.flex

parser.java: cminus.cup
	#$(CUP) -dump -expect 3 cminus.cup
	$(CUP) -expect 3 cminus.cup

clean:
	rm -f parser.java Lexer.java sym.java *.class absyn/*.class absyn/Decs/*.class absyn/Exps/*.class absyn/Lists/*.class absyn/Vars/*.class absyn/VarDecs/*.class *~

