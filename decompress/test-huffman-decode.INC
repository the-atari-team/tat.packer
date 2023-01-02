; Quelle: http://forum.6502.org/viewtopic.php?t=4642
; Decode one symbol from a cannonical huffman encoded bistream
; (To decode multiple symbols this have to be called within a loop)
; Note that some variables must be inintialized before decoding the 1st symbol

        .local

HuffPointer=@REG

Get_Huff_Value
        lda #$00
        sta nbits
        sta code_l
        sta code_m
;        sta code_h              ; Needed only if some codes are longer than 16-bit
        sta match_code_l
        sta match_code_m
;        sta match_code_h        ; Needed only if some codes are longer than 16-bit

main_loop:
        inc nbits

        jsr Read_bit_from_stream        ;Read one bit and shift in the current code variable
        rol code_l
        rol code_m
;        rol code_h

        asl match_code_l
        rol match_code_m
;        rol match_code_h

        ; Search if the read code correspond to something...
        ldy #$00
search_loop:
        lda Huffman_length_tbl, Y          ; Search for a node that has the current length
        cmp nbits
        bne not_found

found_length:
        lda match_code_l
        cmp code_l
        bne code_wrong
        lda match_code_m
        cmp code_m
        bne code_wrong
;        lda match_code_h
;        cmp code_h
;        bne code_wrong

found:
        tya                ; Value in Y
        rts

code_wrong:
        inc match_code_l        ; If the code is not this one, match_code is incremented by '1' and continue searching
        bne not_found
        inc match_code_m
;        bne not_found
;        inc match_code_h

not_found:
        iny                    ; Next code
        bne search_loop        ; Continue for searching for same length
        beq main_loop          ; Searching for a longer length

; Read_bit_from_stream:         ; Read a single bit from input stream and return it in C flag
;         dec HuffBitCtr
;         bpl _not_next_byte
; 
;         lda #$07
;         sta HuffBitCtr
;         inc HuffPointerL
;         bne +
;         inc HuffPointerH
; 
; +       lda (HuffPointerL),Y  ; Read next byte in shift register
;         sta HuffShiftReg
; _not_next_byte
;         lsr HuffShiftReg
;         rts
; 

Read_bit_from_stream:          ; Read a single bit from input stream and return it in C flag
        lsr HuffShiftReg
        bne _not_next_byte

        lda (HuffPointerL),Y   ; Read next byte in shift register
        inc HuffPointerL
        bne ?nothigh
        inc HuffPointerH
?nothigh
        sec
        ror a
        sta HuffShiftReg
_not_next_byte
        rts
