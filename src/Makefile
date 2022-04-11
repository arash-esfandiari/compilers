JAVA=java
JAVAC=javac
JFLEX=jflex
#JFLEX=~/Projects/jflex/bin/jflex
CLASSPATH=-cp ../java-cup-11b.jar:.
CUP=$(JAVA) $(CLASSPATH) java_cup.Main


all: CM.class

CM.class: absyn/*.java absyn/Decs/*.java absyn/Exps/*.java absyn/Lists/*.java absyn/Vars/*.java absyn/VarDecs/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java SemanticAnalyzer.java Scanner.java CM.java

%.class: %.java
	$(JAVAC) $(CLASSPATH) $^

Lexer.java: cminus.flex
	$(JFLEX) cminus.flex

parser.java: cminus.cup
	#$(CUP) -dump -expect 3 cminus.cup
	$(CUP) -expect 3 cminus.cup

clean:
	rm -r -f Output parser.java Lexer.java sym.java *.class absyn/*.class absyn/Decs/*.class absyn/Exps/*.class absyn/Lists/*.class absyn/Vars/*.class absyn/VarDecs/*.class *~

