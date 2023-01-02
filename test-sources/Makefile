CC=wnfc
ASM=atasm
ATARI800_OPTIONS='-kbdjoy0'

WNFC_OPTIONS='-O 2'

.PHONY: copy_turbobasic_files prepare_atari clean all prepare basic_tests

# You need to give the "PROGRAM name" name out of wnf file here, add .65o

TESTS=\
test-5bytes.65o


INCLUDES=

ADDITIONALS=

all: $(INCLUDES) $(TESTS)

clean::
	rm -f $(ADDITIONALS) $(TESTS) $(INCLUDES)
	rm -f *.COM *.ASM *.65o .insert.txt *.lab


TESTWNF.65o: test-wnf.wnf
	$(CC) $< $(WNFC_OPTIONS) -I ..
	$(ASM) $(@:.65o=.ASM) -l$(@:.65o=.lab) >/dev/null
	cp $@ $(@:.65o=.COM)

test-5bytes.65o: test-5bytes.ASM
	$(ASM) $(@:.65o=.ASM)
	cp $@ $(@:.65o=.COM)


help:
	@echo "Makefile for the Atari 8bit test files for packer"
	@echo "usage:"
	@echo "make             - to create all test files"
	@echo "make clean       - remove all build files"
	@echo "make help        - show this list"

