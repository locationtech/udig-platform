EditBlackboard
==============

EditBlackboard
~~~~~~~~~~~~~~

Das EditBlackboard ist, vereinfacht gesagt, ein Behälter für die zu bearbeitenden Features. Mittels
der Bearbeitungswerkzeuge können ihm Features hinzugefügt oder entfernt werden.

-  Klicken auf ein nicht gewähltes Features löscht das EditBlackboard und fügt nur dieses Feature
   hinzu (wählt es aus).
-  Shift+Klick (auf Feature) fügt weitere Features hinzu.
-  Strg+Klick (auf Features) fügt nicht ausgewählte Features hinzu und entfernt bereits ausgewählte
   Features. Mit anderen Worten, jeder Klick schaltet zwischen "ausgewählt" und "nicht ausgewählt"
   um.

Warum sollte man Features zum EditBlackboard hinzufügen?

#. Das Umschalten zwischen Features im EditBlackboard (z.B. zur Bearbeitung) ist viel schneller als
   das Umschalten zu nicht ausgewälten Features. Zum einen muß das System nicht mit dem jeweiligen
   Dienst kommunizieren, zum anderen werden Änderungen nicht auf die Features übertragen.
#. Eckpunkte, die von mehreren der gewählten Features gemeinsam verwendet werden, können auch
   gemeinsam verschoben werden - so als wäre der Layer ein "Coverage" (eine Topologie ohne Lücken).
#. Es gibt einen Einrastmodus, der nur dar Features im EditBlackboard berücksichtigt. Dies erlaubt
   Nutzern, die Features auszuwählen, auf die ein Einrasten möglich sein soll. Dadurch erhöht sich
   auch die Geschwindigkeit des Einrastens, da nicht der gesamte originale Datensatz durchsucht
   werden muß.

(c) Copyright (c) 2004-2008 Refractions Research Inc. and others.
