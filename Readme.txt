Tema 2 - Algoritmi Paraleli si Distribuiti
Florin-Razvan Mihalache
336 CB

CommunicationChannel
Pentru implementarea acestei clasei, am pornit de la laboratorul 7, mai exact
de la Multiple Producers Multiple Consumers, unde am folosit un ArrayBlockingQueue.
In acest caz, am folosit doua ArrayBlockingQueue in care pastrez mesajele minerilor,
respectiv ale vrajitorilor. Am folosit doua ReentrantLock, unul pentru cand trimit
mesaj la un vrajitor, celalalt cand primesc mesaj de la un vrajitor.
In putMessageMinerChannel si in getMessageMinerChannel pur si simplu pun mesaj in
ArrayBlockingQueue pentru a trimite unui miner, respectiv iau mesajul de la un 
miner din ArrayBlockingQueue.
In putMessageWizardChannel si in getMessageWizardChannel pun mesajul in ArrayBlockingQueue
pentru a trimite unui vrajitor, respectiv iau mesajul de la un vrajitor din ArrayBlockingQueue,
insa in ambele metode am folosit cate un ReentrantLock pentru sincronizare, 
pentru a evita suprapunerea de mesaje de la vrajitori. In ambele metode, dau lock, 
ca sa sincronizez, trimit sau iau mesajul de la vrajitor si verific daca am mesaj 
de tip END sau EXIT (daca da, dau unlock). Daca avem 2 mesaje de tip vrajitori (camera
parinte si camera curenta), vom da unlock de doua ori (atatea mesaje sunt adica).

Miner
Aici, intr-un infinite loop, pe modelul din clasa Wizard, iau primul mesaj de la
vrajitor (aka tip camera parinte) si verific daca e de tip EXIT sau END (EXIT ->
ies din loop, adica programul se termina, END -> dau skip la ce urmeaza in cod,
ca am ajuns intr-un punct terminal). Daca nu intru in aceste conditii, iau al
doilea mesaj de la vrajitor (camera curenta) si verific daca camera curenta a
fost vizitata deja (pe model DFS) -> daca nu, adaug ID-ul camerei in Set-ul
solved, hash-uiesc mesajul de la vrajitor si trimit un mesaj inapoi, cu mesajul
hash-uit.