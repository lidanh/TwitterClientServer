###################################################################################################################
#
#        Dynamic makefile, by Ken Saggy and Lidan Hifi
#        Will automatically search for files ending with SRCEXT (source extension) in SRCDIR (source directory)
#
#        MAKE FILE DOCUMENTATION:
#        ========================
#        CC         = C Compiler [g++]
#        CFLAGS     = Flags used during compilation
#        SRCDIR     = Source Directory [src]
#        BUILDDIR   = Where the object files will be placed [bin]
#        TARGET     = Name of the final target executable
#        SRCEXT     = File extension to look for of source code files [.cpp]
#        OBJECTS    = Objects files (searched for in BUILDDIR)
#        INC        = Flags used to include "include" directory duing compilation
#        LFLAGS     = Flags used for linkning
#
####################################################################################################################

CC := g++
SRCDIR := src
BUILDDIR := bin
TARGET := bin/twitterClient
APP := twitterClient

SRCEXT := cpp
SOURCES := $(shell find $(SRCDIR) -type f -name *.$(SRCEXT))
OBJECTS := $(patsubst $(SRCDIR)/%,$(BUILDDIR)/%,$(SOURCES:.$(SRCEXT)=.o))
CFLAGS := -g -Wall -Weffc++
LFLAGS := -L/usr/lib -lboost_system -lboost_locale -lboost_thread -lPocoFoundation -lPocoUtil
#INC := -Linclude

all: $(TARGET)

$(TARGET): $(OBJECTS)
	@echo "Building target: $(TARGET)"
	@echo " Linking..."
	@echo " $(CC) $^ -o $(TARGET) $(LFLAGS)"; $(CC) $^ -o $(TARGET) $(LFLAGS)
	@echo "Finished building target: $(TARGET)"

$(BUILDDIR)/%.o: $(SRCDIR)/%.$(SRCEXT)
	@mkdir -p $(@D); # $(@D) returns the directory part of the path of $@ (the target name)
	@echo " $(CC) $(CFLAGS) $(INC) -c -o $@ $<"; $(CC) $(CFLAGS) $(INC) -c -o $@ $<

clean:
	@echo " Cleaning...";
	@echo " $(RM) -r $(BUILDDIR) $(TARGET)"; $(RM) -r $(BUILDDIR)/* $(TARGET)

# Tests
#tester:
#  $(CC) $(CFLAGS) test/tester.cpp $(INC) $(LFLAGS) -o bin/tester
