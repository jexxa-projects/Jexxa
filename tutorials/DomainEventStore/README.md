
# Domain Event vs. Event Sourcing 
https://www.innoq.com/en/blog/domain-events-versus-event-sourcing/



# Production systems 

reference https://www.britannica.com/technology/production-system

There are three common types of basic production systems: 
*   The batch system:
    In a batch system, general-purpose equipment and methods are used to produce small quantities of output (goods or services) with specifications that could vary greatly from one batch to the next. Examples include systems for producing specialized machine tools or heavy-duty construction equipment, specialty chemicals, and processed food products
    
*   The continuous system:
    In the continuous system, items to be processed flow through a series of steps, that are common to most other products being processed. Since large volumes of throughput are expected, specially designed equipment and methods are often used so that lower production costs can be achieved. Examples include systems for assembling automobile engines and automobiles themselves, as well as other consumer products such as televisions, washing machines, and personal computers. Therefore, continuous production systems are often referred to as assembly systems or assembly line systems and, as noted below, are common in mass production operations.
    
*   The project system:
    The third type of production system is the project, or “one-shot” system. For a single, one-of-a-kind product, for example, a building, a ship, or the prototype of a product such as an airplane. Because of the singular nature of project systems, special methods of management have been developed to contain the costs of production within reasonable levels.
    

Note, that batch and continues systems are often found in combination. 

## Batch Process / Production 

reference: https://de.wikipedia.org/wiki/Chargenprozess 
In the manufacturing batch production process, the machines are in chronological order directly related to the manufacturing process. The batch production method is also used so any temporary changes or modifications can be made to the product if necessary during the manufacturing process.[3] For example, if a product needed a sudden change in material or details changed, it can be done in between batches. A

Batch production process: A production process that results in the production of defined quantities of material by subjecting quantities of materials to be used are subjected to an orderly sequence of process activities using one or more pieces of manufacturing equipment within a delineated time period. 

In terms of DDD the most important aspects are:

* Defined quantities of materials to be used 
* Using one or more pieces of manufacturing equipment
* Within a defined period of time
* An ordered sequence of process activities.








### Archive 

Vorab:

• Aus meiner Sicht ergeben sich aktuell 2(-3) Arten von DDD-Repositories für neue Anwendungen aufbauend auf Jexxa (Anbindung an Bestands-DBs sind in den folgenden Überlegungen erstmal ausgeschlossen)
1. Repositories für Produktionsdaten => Ziel: Anwendungszustand kann nach Neustart der Anwendung rekonstruiert werden. (Kann aber prinzipiell bei einer Putzschicht komplett gelöscht werden. ) Daten sind nur innerhalb des Micro-Services relevant.
2. Repositories Produktionsarchiv: Ziel: Persistiert die abgeschlossenen Geschäftsereignisse(Domain-Events): Leitet evtl. auch aus der Summe von mehreren DE-Events neue ab (z.B. RE-Charge produziert). Informationen werden von Berichtswesen und nachfolgenden Prozessschritten verwendet. Diese Daten sind kritisch und können so ohne weiteres nicht gelöscht werden ... => Ist hauptsächlich nur außerhalb des eigenen Kontextes relevant.
3. Evtl:Repositories für Rohdaten erfassen von L1. Man weiß nicht ob man die Daten irgendwann nochmal braucht und hebt sie deswegen auf. Unklar, ob dies überhaupt ein 'DDD-Repository' ist...

• Die jeweilige Art des Repositories sollte auch in der Software erkennbar sein.
• Zu 1. Sollte ein Key-Value Repository ausreichend sein.
• Zu 2. Ist momentan so nicht unterstützt.
• Zu 3. Haben wir meines Wissens beim RE-Projekt nicht gebraucht. Evtl. reicht dann aber auch ein einfacher Key-Value Store, da man sowieso nicht weiß welche Daten irgendwann mal relevant werden.

• Annahmen zu Repositories für Produktionsarchiv
• Bei einem Produktionsarchiv reicht eine einfache Key-Value Suche nicht aus, da wir z.B. auch über Teilinformationen suchen müssen (z.B. Zeitraum) => Suche nur über bei Projektumsetzung bekannte Use-Cases zur Produktionsunterstütztung (z.B. Berichtswesen)
• Erfasste Produktionsdaten sind recht 'generisch' d.h. wir kennen den Aufbau der/aller DomainEvents nicht komplett. Wir kennen nur die Such-Parameter wie Z.b. RE-Chargennummer, Zeitstempel,... => Eine Objekt-Notation wie z.B. JSON ist erforderlich ist.
• Such-Keys für Produktionsunterstützung sind meiner Einschätzung nach bei der Implementierung bekannt. Änderungen für Produktionsunterstützung sind in der Zukunft eher selten. Wenn, dann ist es Fachlogik für die wohl auch ein neues Aggregate erforderlich ist (z.B. es muss ab sofort irgendetwas neues quittiert werden)
• Such-Keys auf echten Archiv-Daten kommt eher aus Forschung => Diese User-Cases werden nur insofern berücksichtigt, dass sie die JSON-Repräsentation abfragen können. Auswertungen müssen dann selbst gemacht werden
• TI-Entwickler kennt sich momentan (nur) mit klassischen Features relationaler Datenbanken aus. => Erste Implementierung mit klassischen JDBC und nicht mit JSON-Features der relationalen DBs.

Meine (Haupt-)Motivation des MultiIndexRepository:

• Explizite Repräsentation dieser Art von Repositories durch Repository-Typ/Namen, um Divide & Conquer der Daten zu ermöglichen und zu unterstützen. Nur diese Daten sollten außerhalb ihres Kontextes relevant sein und angeboten werden.
• Einführen einer API welche die zum Zeitpunkt der Entwicklung bekannten Use-Cases unterstützt so dass sie
• Von einem 'Junior' Entwickler verwendbar ist
• Falsche Verwendung gerade der Such-Parameter bei einem Review einfach erkennbar macht (Bsp.: Ich speichere mein Objekt als Json + alle Felder des Objekts auch noch in separaten Spalten um sicher zu sein...)
• Mindset für Black-Box Tests unterstützen. (Wenn ich sowieso schon mit JDBC direkt auf die DB gehe, MUSS ich mir ja auch in meinen Tests meinen Datenhaushalt manuell in der DB erstellen, oder Mock verwenden, oder ....)
• Abgrenzung: Zukünftige Use-Cases die nicht direkt produktionskritisch/unterstützend sind werden nur insofern berücksichtigt, dass man die JSON-Notation (wie auch immer) zugänglich machen kann.

Daher bräuchte ich nochmal Rückmeldung von euch zu folgenden Punkten:

• Sind die Annahmen zu Repositories nachvollziehbar und passend?
• Wie oft kam es in der Vergangenheit bei Phoenix vor, dass Tabellen um weitere Spalten erweitert werden mussten, um produktionsunterstützende Abläufe umzusetzen? Wenn ja, welche? (Hier interessieren mich nicht Erweiterungen, die erforderlich waren damit neue Kontexte Daten reinschreiben konnten)

• Zur API: Im Anhang findet Ihr die initiale Version der Repository-Implementierung (JdbcProzessAbweichungRepository), sowie die 'optimierte' Version. Hierbei wäre folgendes wichtig:
• Versteht man die neue Implementierung 'besser' / einfacher
• Ist der Ansatz mit den Such-Strategien über Enums nachvollziehbar und für Reviews gut geeignet bzw. ausreichend explizit ?
• Ein Beispiel mit mehreren Strategien findet ihr unter
• https://github.com/repplix/Jexxa/blob/2.7.0-SNAPSHOT/jexxa-core/src/test/java/io/jexxa/infrastructure/drivenadapterstrategy/persistence/jdbc/experimental/MultiIndexRepositoryTest.java
• Falls euch die Implementierung interessiert: https://github.com/repplix/Jexxa/tree/2.7.0-SNAPSHOT/jexxa-core/src/main/java/io/jexxa/infrastructure/drivenadapterstrategy/persistence/jdbc/experimental

