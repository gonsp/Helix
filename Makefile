
ROOT 		= $(PWD)
SRCDIR		= $(ROOT)/src/Helix
RESDIR		= $(ROOT)/resources
MAIN_SRC	= $(SRCDIR)/Main.java
INTERPDIR	= $(SRCDIR)/interpreter
PARSERDIR	= $(SRCDIR)/parser
GRAMMAR 	= $(PARSERDIR)/Helix.g
PARSER_SRC 	= $(PARSERDIR)/HelixLexer.java \
			  $(PARSERDIR)/HelixParser.java
BINDIR 		= $(ROOT)/bin
LIBDIR		= $(ROOT)/lib
CLASSDIR 	= $(BINDIR)/classes
LIB_ANTLR	= $(LIBDIR)/antlr-3.4-complete.jar
CLASSPATH 	= $(shell find $(LIBDIR)/* | grep .jar | tr -s '\n' ':')
	
EXEC		= $(BINDIR)/helix_parser
MANIFEST 	= $(BINDIR)/Manifest.txt
JARFILE 	= $(BINDIR)/helix_parser.jar

EXAMPLESDIR	= $(ROOT)/examples

JFLAGS		= -classpath $(CLASSPATH) -d $(CLASSDIR)



all: helix

helix: $(GRAMMAR) $(MAIN_SRC)

	java -jar $(LIB_ANTLR) -o $(PARSERDIR) $(GRAMMAR)
	rm $(PARSERDIR)/*.tokens

	if [ ! -e $(BINDIR) ]; then\
		mkdir $(BINDIR);\
		mkdir $(CLASSDIR);\
	fi

	javac $(JFLAGS) $(MAIN_SRC) $(PARSER_SRC) $(shell find $(INTERPDIR)/* | grep .java)

	echo "Main-Class: Helix.Main" > $(MANIFEST)
	printf "Class-Path: " >> $(MANIFEST)
	cd $(BINDIR); find ../lib | grep .jar | tr '\n' ' ' | sed '$ s/.$$/?/' | tr '?' '\n' >> $(MANIFEST)
	cp javax.usb.properties $(CLASSDIR)/javax.usb.properties
	cp -r $(RESDIR) $(CLASSDIR)
	cd $(CLASSDIR); jar -cmf $(MANIFEST) $(JARFILE) *
	echo "#!/bin/bash" > $(EXEC)
	echo 'exec java -enableassertions -jar $(JARFILE) "$$@"' >> $(EXEC)
	chmod a+x $(EXEC)

# example call: make test filename=hello_helix
test: $(EXEC) $(EXAMPLESDIR)/$(filename).hx
	$(EXEC) -dot $(EXAMPLESDIR)/$(filename).hx
	dot -Tpdf -O $(EXAMPLESDIR)/$(filename).hx.dot
	rm $(EXAMPLESDIR)/$(filename).hx.dot
	mv $(EXAMPLESDIR)/$(filename).hx.dot.pdf $(EXAMPLESDIR)/$(filename).pdf
	display $(EXAMPLESDIR)/$(filename).pdf

clean:
	rm -rf $(BINDIR) $(PARSER_SRC)
