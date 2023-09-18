1 REM ***********************************
2 REM * EXEMPLO GOSUB - DATA 19/09/2017
3 REM ***********************************
4 REM
10 PRINT "INICIO"
20 FOR I=1 TO 10
30 GOSUB 60
40 NEXT
50 END
55 REM *****************
60 REM INICIO DE FUNCAO
65 REM *****************
70 PRINT "PASSOU "+I
80 RETURN
