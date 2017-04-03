
ROOT 		= $(PWD)
SRCDIR		= $(ROOT)/src
GRAMMAR 	= $(SRCDIR)/Drone.g
PARSERDIR	= $(SRCDIR)/generated
MAIN_SRC	= $(SRCDIR)/Main.java
PARSER_SRC 	= $(PARSERDIR)/DroneLexer.java \
			  $(PARSERDIR)/DroneParser.java
BINDIR 		= $(ROOT)/bin
LIBDIR		= $(ROOT)/lib
CLASSDIR 	= $(BINDIR)/classes
LIB_ANTLR	= $(LIBDIR)/antlr-3.4-complete.jar
LIB_CLI		= $(LIBDIR)/commons-cli-1.2.jar
JARPATH 	= "$(LIB_ANTLR) $(LIB_CLI)"
CLASSPATH 	= $(LIB_ANTLR):$(LIB_CLI)

EXEC		= $(BINDIR)/drone_parser
MANIFEST 	= $(BINDIR)/Manifest.txt
JARFILE 	= $(BINDIR)/drone_parser.jar

JFLAGS		= -classpath $(CLASSPATH) -d $(CLASSDIR)


all: parser

parser: $(GRAMMAR) $(MAIN_SRC)

	java -jar $(LIB_ANTLR) -o $(PARSERDIR) $(GRAMMAR)
	rm $(PARSERDIR)/*.tokens

	if [ ! -e $(BINDIR) ]; then\
		mkdir $(BINDIR);\
		mkdir $(CLASSDIR);\
	fi
	javac $(JFLAGS) $(MAIN_SRC) $(PARSER_SRC)

	echo "Main-Class: Drone.Main" > $(MANIFEST)
	echo "Class-Path: $(JARPATH)" >> $(MANIFEST)
	cd $(CLASSDIR); jar -cmf $(MANIFEST) $(JARFILE) *
	printf "#!/bin/sh\n\n" > $(EXEC)
	printf 'exec java -enableassertions -jar $(JARFILE) "$$@"' >> $(EXEC)
	chmod a+x $(EXEC)

clean:
	rm -rf $(BINDIR) $(PARSERDIR)