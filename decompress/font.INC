; for WiNiFe-Compiler
; (cdw) by TAT 2022
;

; sichern der aktuellen Adresse
FONT_OLD_ADDRESS=*

  *=$B800 ; - $BBA0 -1 (928 Bytes!) Zeichen 0 - 115
  ; $bb7e beginnt die Displaylist von Graphics 12
  ; $bba0 beginnt der Screen
; Enthaelt:
; - Spielfeld
; TODO: ist nicht ganz optimiert.
 .include "NIGHTFNT.INC"

; wiederherstellen der aktuellen Adresse
  *=FONT_OLD_ADDRESS
