INCLUDES := $(INCLUDES) -Isrc -Iext/src
EXTRA_LIBS ?= -l$(NAME)-ext

CPPFLAGS := $(CPPFLAGS) $(INCLUDES)
CFLAGS := $(CFLAGS) -g -pipe
CXXFLAGS := $(CFLAGS) -std=gnu++11

BIN := bin
OBJ := obj

NAME := SetOfLines

LIB = lib$(NAME).a
EXT_LIB = lib$(NAME)-ext.a

LIBS = $(BIN)/$(LIB) $(BIN)/$(EXT_LIB)

SRC = src
STUB = stub
NATIVE = native

SRCS = \
	src/setoflines/Bucket.cpp \
	src/setoflines/Line.cpp \
	src/setoflines/Pair.cpp \
	src/setoflines/Point.cpp \
	src/setoflines/PotentialLine.cpp \
	src/setoflines/SetOfLines.cpp \


STUB_SRCS = \


NATIVE_SRCS = \


EXT_SRCS = \


EXT_STUB_SRCS = \

EXT_NATIVE_SRCS = \

MAIN_SRCS = \


MAINS = $(MAIN_SRCS:$(SRC)/%-main.cpp=$(BIN)/%)
OBJS = $(SRCS:$(SRC)/%.cpp=$(OBJ)/%.o)
STUB_OBJS = $(STUB_SRCS:$(STUB)/%.cpp=$(OBJ)/%.o)
NATIVE_OBJS = $(NATIVE_SRCS:$(NATIVE)/%.cpp=$(OBJ)/%.o)

EXT_OBJS = $(EXT_SRCS:ext/$(SRC)/%.cpp=$(OBJ)/%.o)
EXT_STUB_OBJS = $(EXT_STUB_SRCS:ext/$(STUB)/%.cpp=$(OBJ)/%.o)
EXT_NATIVE_OBJS = $(EXT_NATIVE_SRCS:ext/$(NATIVE)/%.cpp=$(OBJ)/%.o)

all: $(LIBS)

define cc-command
@mkdir -p $(dir $@); 
@echo Compile $<; $(CXX) $(CPPFLAGS) $(CXXFLAGS) -c -o $@ $<
endef

$(OBJS):$(OBJ)/%.o: $(SRC)/%.cpp | print-opts
	$(cc-command)
	
$(STUB_OBJS):$(OBJ)/%.o: $(STUB)/%.cpp | print-opts
	$(cc-command)

$(NATIVE_OBJS):$(OBJ)/%.o: $(NATIVE)/%.cpp | print-opts
	$(cc-command)

$(EXT_OBJS):$(OBJ)/%.o: ext/$(SRC)/%.cpp | print-opts
	$(cc-command)

$(EXT_STUB_OBJS):$(OBJ)/%.o: ext/$(STUB)/%.cpp | print-opts
	$(cc-command)

$(EXT_NATIVE_OBJS):$(OBJ)/%.o: ext/$(NATIVE)/%.cpp | print-opts
	$(cc-command)

%.a:
	@echo Archive $@
	@mkdir -p $(dir $@)
	@rm -f $@
	@ar rcs $@ $^

$(BIN)/$(LIB): $(OBJS) $(STUB_OBJS) $(NATIVE_OBJS)

$(BIN)/$(EXT_LIB): $(EXT_OBJS) $(EXT_STUB_OBJS) $(EXT_NATIVE_OBJS)

$(MAINS):$(BIN)/%:$(SRC)/%-main.cpp $(LIBS)
	@mkdir -p $(dir $@); 
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) -L$(BIN) -o $@ $< -l$(NAME) $(EXTRA_LIBS)

mains: $(MAINS)

clean:
	rm -rf $(OBJ) $(BIN)

print-opts:
	@echo Building with \"$(CXX) $(CPPFLAGS) $(CXXFLAGS)\"
	
.PHONY: all mains clean print-opts
