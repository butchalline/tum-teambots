package teambot.common.interfaces;

import java.io.IOException;

public interface IUsbIO {
	
	public int read(byte[] buffer) throws IOException;
	public void write(byte[] buffer) throws IOException;
}

//TODO
//[20:37:29] pcaaron: btw.
//[20:37:40] pcaaron: eben festgestellt dass ich z.Z. nur 1 Paket pro loop aufruf empfange xD
//[20:37:54] pcaaron: und n pakete verschicke
//[20:38:24] pcaaron: problem ist aber wenn ich mehrere pakete empfange und von oben gespamt wird er wohl endlos am empfangen ist
//[20:38:36] pcaaron: nur 1 paket = feature oder failure?
//[20:39:00] Alex Reimann: kommt drauf an was runter geschickt wird
//[20:39:09] Alex Reimann: wie werden die abgearbeitet
//[20:39:27] Alex Reimann: wenn man jetzt z.b. zwei mode-wechsel nacheinander runter schickt
//[20:39:40] Alex Reimann: und die beiden pakete bearbeitet werden würden
//[20:40:13] pcaaron: mehr oder weniger sofort... also wenn du ein mode wechsel schickst wird => request state aufgerufen => also ist "requestet state" dein state... also wechselt die statemachine da rein und führt es aus
//[20:40:38] Alex Reimann: und ich mein wenn du jetzt mehrere bearbeiten würdest
//[20:40:43] pcaaron: wenn ich nun mehrere pakete auslese würden die sich überschreiben
//[20:40:46] pcaaron: jup
//[20:40:48] Alex Reimann: k
//[20:41:09] Alex Reimann: es staut sich halt sonst auf der line
//[20:41:17] pcaaron: die frage ist auch ob es sinn macht 10x eine geschwindigkeitsvorgabe auszulesen
//[20:41:57] pcaaron: ja also sagen wir mal du hast 20 geschwindigkeitsvorgaben gespammt... warum auch immer...
//[20:42:03] pcaaron: und willst dann "command stop" schicken
//[20:42:19] pcaaron: dann würde die loop erst 20 geschwindikeitsvorgaben lesen bevor sie stoppen würde
//[20:43:02] pcaaron: aber ich kann das ja irgendwie schwer umgehen... wenn ich nun ein weiteres paket auslese und das ist schon das nächste ^^ und dann wieder
//[20:43:07] pcaaron: ... kommt halt drauf an wie schnell die kommne
//[20:43:55] Alex Reimann: kommt glaub drauf an worauf wir den fokus setzen wollen
//[20:44:18] pcaaron: letzendlich wenn ich mehrere auslese (was wohl das ganze langsamer macht) ... dann müsstest du bei nem state wechsel auf ein status ack warten bevor du ein nächsten state wechsel willst
//[20:44:18] Alex Reimann: sicherstellen dass der zyklus läuft
//[20:44:34] Alex Reimann: oder dass das zeugs abgerufen wird
//[20:44:49] pcaaron: ich denke zyklus ist wichtiger wegen reglung und allem
//[20:44:53] Alex Reimann: joah
//[20:44:53] pcaaron: aber wenn wichtiges packet kommt
//[20:44:55] Alex Reimann: also feature
//[20:44:58] pcaaron: und du gespammt hast ^^
//[20:45:00] pcaaron: dann puff
//[20:45:01] pcaaron: xD
//[20:45:07] pcaaron: gegen die wand gefahren
//[20:45:08] Alex Reimann: spamm halt nicht
//[20:45:17] Alex Reimann: die bumper stoppen unten
//[20:45:21] Alex Reimann: sollten unten stoppen
//[20:45:24] pcaaron: ^^ ja
//[20:45:29] pcaaron: ... das war mein vorschlag
//[20:45:35] pcaaron: ein paar meinten das müsse vom handy kommen
//[20:45:42] pcaaron: ... daher ein modus... bumper deaktiviert
//[20:45:51] pcaaron: wenn du durch ne enge tür willst oder sowas
//[20:45:58] Alex Reimann: ja schwierig
//[20:46:01] Alex Reimann: wobei
//[20:46:08] Alex Reimann: ne wir fahren nur nach vorne
//[20:46:18] Alex Reimann: also wenn die vorne anspringen stop
//[20:46:24] pcaaron: wir können nich seitwärts fahren
//[20:46:25] Alex Reimann: bzw nach hinten fahren wir auch, aber wayne
//[20:46:33] pcaaron: xD
//[20:46:41] pcaaron: ja aber stell dir vor du willst dich drehen
//[20:46:45] pcaaron: und links von dir ist ne wand
//[20:46:52] pcaaron: ... dann drehst du und wand ist da... stopp
//[20:46:54] Alex Reimann: ja..
//[20:46:57] pcaaron: also kannst du dich nicht drehen
//[20:47:01] Alex Reimann: und eine richtung haben wir unten auch nicht
//[20:47:14] pcaaron: richtung?
//[20:47:24] Alex Reimann: in die wir fahren
//[20:47:29] Alex Reimann: wird oben berechnet
//[20:47:42] Alex Reimann: ja müssen wir wohl an und aus runterschicken
//[20:47:51] Alex Reimann: aufschreiben..
//[20:48:00] Alex Reimann: ich haus mal in den code rein
//[20:48:05] pcaaron: naja ich hatte ursprünglich mal vorgesehen, sobald die odometrie unten läuft, dass es einen "turn" modus gibt bei dem du sagst dreh dich um 90° nach links
//[20:48:15] pcaaron: und dann könnte man in dem modus die bumper ignorieren
//[20:48:23] pcaaron: aber wenn wir es so machen wie jetzt
//[20:48:28] pcaaron: immer geschwindigkeitsvorgaben
//[20:48:36] pcaaron: dann müssen wir für turns wohl den state wechseln
//[20:48:46] pcaaron: oder eben wenn wir die bumper kurzzeitig ignorieren wollen