CC=wnfc
ASM=atasm
PACKER=xl-packer

ATARI800_OPTIONS='-kbdjoy0'

FIRMWARE=../../lla.atarixl.programming/firmware
COMPILER=../../lla.atarixl.programming/compiler

WNFC_OPTIONS='-O' 2 '-smallHeapPtr'

.PHONY: copy_turbobasic_files prepare_atari clean all prepare basic_tests

# You need to give the "PROGRAM name" name out of wnf file here, add .65o
GAME_OBJ_FILES=\
UNPACK.65o

INCLUDES=\
NIGHTFNT.INC \
PLYFIELD.INC 

TESTS=\
#LOADTEST.65o \
#DLITEST.65o \
#ENEMYTST.65o \
#DUDETEST.65o \
#ARCHTEST.65o \
#KNIGHTST.65o

ADDITIONALS=\


all: $(ADDITIONALS) $(INCLUDES) $(TESTS) $(GAME_OBJ_FILES) atari_game

clean::
	rm -f .wnffiles.txt
	rm -f $(ADDITIONALS) $(GAME_OBJ_FILES) $(INCLUDES)
	rm -f *.COM *.ASM *.65o .insert.txt *.lab *.log
	rm -f *.ASM.inc NIGHT.lst KNIGHTST.lst
	rm -f UNPACK.lst

clean::
	rm -f start-game.atr
	rm -f dos-sd.atr

UNPACK.65o: test-unpack.wnf huffman-decode.INC $(INCLUDES) compress
	$(CC) $< $(WNFC_OPTIONS) -showvariableusage -I $(COMPILER)
	$(ASM) -ha -s $(@:.65o=.ASM) -g$(@:.65o=.lst) -l$(@:.65o=.lab) >$(@:.65o=.log)
	cp $@ $(@:.65o=.COM)

NIGHTFNT.INC: night-knight-font.wnf
	$(CC) $< -noheader $(WNFC_OPTIONS) -I $(COMPILER)

PLYFIELD.INC: playfield.wnf
	$(CC) $< $(WNFC_OPTIONS) -I $(COMPILER)


prepare_game: prepare
# Dann das Spiel selbst
	touch .wnffiles.txt
	echo "UNPACK.COM -> AUTORUN.SYS" >.wnffiles.txt

prepare_last:
	cat .wnffiles.txt >>.insert.txt
	echo "stop" >>.wnffiles.txt


game_disk: prepare_game prepare_last
	xldir $(COMPILER)/dos-sd.atr insert .insert.txt start-game.atr
#	xldir $(COMPILER)/dos-2.5-ed.atr insert .insert.txt start-game.atr

atari_game: game_disk
	atari800 \
    -hreadwrite \
    -H2 /tmp/atari \
    -xlxe_rom $(FIRMWARE)/ATARIXL.ROM \
    -xl -xl-rev 2 \
    -pal -showspeed -nobasic \
    -windowed -win-width 682 -win-height 482 \
    -vsync -video-accel \
    ${ATARI800_OPTIONS} \
   start-game.atr

# Start Altirra with debug and stop at start address
# /portable searchs for an Altirra.ini
debug: $(INCLUDES) $(GAME_OBJ_FILES) game_disk
	wine /home/lars/.wine/drive_c/atarixl.software/AtariXL_400/Altirra-410b5.exe \
	/portable \
	/debug \
	/debugcmd: ".sourcemode on" \
	/debugcmd: ".loadsym UNPACK.lst" \
	/debugcmd: ".loadsym UNPACK.lab" \
	/debugcmd: "bp 4000" \
	/disk "start-game.atr"

prepare:
	rm -f .wnffiles.txt
	rm -f .insert.txt
	echo ".os.txt -> OS.TXT" >>.insert.txt

n: $(INCLUDES) $(GAME_OBJ_FILES)

start: n atari_game

PLAYFIELDS=\
 PLAYS0.DAT \
 PLAYS1.DAT \
 PLAYS2.DAT \
 PLAYS3.DAT \
 PLAYS4.DAT \
 PLAYS5.DAT \
 PLAYS6.DAT \
 PLAYS7.DAT \
 PLAYS8.DAT \
 PLAYS9.DAT \
 PLAYSA.DAT \
 PLAYSB.DAT  # no. 11

.PHONY: $(PLAYFIELDS)

clean::
	rm -f COMPRESSED-PLAYFIELDS.INC

# compress all playfields
COMPRESSED-PLAYFIELDS.INC: $(PLAYFIELDS)
	$(PACKER) --usepairs --data -of $@ $(PLAYFIELDS)

compress: COMPRESSED-PLAYFIELDS.INC




help:
	@echo "Makefile for the Atari 8bit game Night Knight"
	@echo "usage:"
	@echo "make             - to create the whole game disk"
	@echo "make clean       - remove all build files"
	@echo "make start       - start game in atari800 emulator"
	@echo "make help        - show this list"
	@echo "make test        - run only tests"
