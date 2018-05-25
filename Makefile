######################################
# Makefile para programas java
######################################

JAVA_COMP=javac

all: 
	$(JAVA_COMP) *.java

clean: clean-class clean-c

clean-class:
	rm -R ./*.class

clean-c:
	rm -R ./*.c