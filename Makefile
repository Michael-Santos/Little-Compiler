######################################
# Makefile para programas java
######################################

JAVA_COMP=javac

all: 
	$(JAVA_COMP) *.java

clean: clean-class clean-c

clean-class:
	find $(PWD) -iname "*.class" -exec rm -fr {} \;

clean-c:
	find $(PWD) -iname "*.c" -exec rm -fr {} \;
