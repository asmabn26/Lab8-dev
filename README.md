# LAB 8 — Threads, AsyncTask et Handler

## Description

Ce projet Android a été réalisé dans le cadre du module **Programmation Mobile : Android avec Java**.

L’objectif de ce lab est de comprendre comment exécuter des tâches longues en arrière-plan sans bloquer l’interface graphique de l’application.
L’application montre l’utilisation de trois mécanismes importants en Android :

* `Thread` pour exécuter une tâche en arrière-plan.
* `Handler` pour revenir au thread principal et mettre à jour l’interface.
* `AsyncTask` pour simuler un calcul lourd avec une progression de 0 % à 100 %.

L’application permet aussi de tester que l’interface reste réactive grâce à un bouton Toast.

---

## Objectifs du lab

Ce lab permet de comprendre :

* la différence entre **UI Thread** et **Worker Thread** ;
* pourquoi il ne faut pas exécuter un traitement long dans le thread principal ;
* comment utiliser `Thread` avec `Runnable` ;
* comment mettre à jour l’interface avec `Handler(Looper.getMainLooper()).post(...)` ;
* comment utiliser `AsyncTask` pour afficher une progression ;
* comment éviter l’erreur classique `CalledFromWrongThreadException`.

---

## Fonctionnalités de l’application

L’application contient une interface simple avec :

* un tableau de bord affichant l’état actuel de l’application ;
* une barre de progression horizontale ;
* un pourcentage de progression ;
* une zone d’aperçu contenant le logo Android ;
* un bouton pour lancer un chargement avec `Thread` ;
* un bouton pour lancer un calcul avec `AsyncTask` ;
* un bouton pour afficher un Toast et vérifier que l’interface reste réactive.

---

## Aperçu de l’application

L’interface est organisée sous forme de cartes :

* **Tableau de bord** : affiche l’état actuel.
* **Progression** : affiche la barre de progression et le pourcentage.
* **Aperçu** : affiche l’image de test.
* **Actions** : contient les boutons de test.

---

## Démonstration vidéo

La vidéo de démonstration montre :

1. le lancement de l’application ;
2. le chargement de l’image avec un `Thread` ;
3. la réactivité de l’interface avec le bouton Toast ;
4. l’exécution du calcul lourd avec `AsyncTask` ;
5. la progression jusqu’à 100 % ;
6. l’affichage du résultat final.



https://github.com/user-attachments/assets/faae031a-c85e-4b70-810b-791b04c61a70



## Structure du projet

```text
AsyncThreadMonitor/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/asyncthreadmonitor/
│   │   │   │   └── MainActivity.java
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── activity_main.xml
│   │   │   │   ├── drawable/
│   │   │   │   │   └── circle_green.xml
│   │   │   │   ├── mipmap/
│   │   │   │   │   └── ic_launcher
│   │   │   │   └── values/
│   │   │   │       └── strings.xml
│   │   │   │
│   │   │   └── AndroidManifest.xml
│
└── README.md
```

---

## Explication du fonctionnement

### 1. UI Thread

Le **UI Thread**, aussi appelé **Main Thread**, est le thread principal de l’application Android.

Il est responsable de :

* l’affichage des composants graphiques ;
* la gestion des clics ;
* l’exécution de méthodes comme `onCreate()` ;
* la mise à jour des vues comme `TextView`, `ImageView` et `ProgressBar`.

Si une tâche longue est exécutée directement dans ce thread, l’application peut se bloquer et Android peut afficher une erreur de type **ANR**.

---

### 2. Worker Thread

Un **Worker Thread** est un thread secondaire utilisé pour exécuter une tâche longue en arrière-plan.

Dans ce projet, il est utilisé pour simuler le chargement d’une image :

```java
Thread worker = new Thread(() -> {
    // tâche longue
});
worker.start();
```

La méthode `start()` permet de démarrer réellement le thread.

---

### 3. Handler

Un thread secondaire ne peut pas modifier directement l’interface graphique.

Par exemple, il ne faut pas modifier directement un `TextView`, une `ProgressBar` ou une `ImageView` depuis un Worker Thread.

Pour revenir au thread principal, ce projet utilise :

```java
uiHandler = new Handler(Looper.getMainLooper());
```

Puis :

```java
uiHandler.post(() -> {
    imgPreview.setImageResource(R.mipmap.ic_launcher);
    progressBar.setProgress(100);
    txtProgressPercent.setText("100 %");
    txtStatus.setText(getString(R.string.status_image_loaded));
});
```

Cette solution permet d’exécuter le code de mise à jour graphique dans le **UI Thread**.

---

### 4. AsyncTask

`AsyncTask` est utilisé dans ce lab pour comprendre la logique d’un traitement en arrière-plan avec progression.

Il contient plusieurs méthodes :

```java
onPreExecute()
doInBackground()
onProgressUpdate()
onPostExecute()
```

Rôle de chaque méthode :

| Méthode              | Rôle                                    |
| -------------------- | --------------------------------------- |
| `onPreExecute()`     | Prépare l’interface avant le traitement |
| `doInBackground()`   | Exécute le calcul lourd en arrière-plan |
| `publishProgress()`  | Envoie la progression                   |
| `onProgressUpdate()` | Met à jour la ProgressBar               |
| `onPostExecute()`    | Affiche le résultat final               |

Dans ce projet, `AsyncTask` fait avancer la progression de 0 % à 100 %, puis affiche le résultat du calcul.

---

## Tests réalisés

### Test 1 — Chargement avec Thread

Action :

```text
Cliquer sur : Démarrer chargement avec Thread
```

Résultat attendu :

```text
État : image chargée avec succès
Progression : 100 %
```

Ce test montre que le chargement est exécuté en arrière-plan.

---

### Test 2 — Réactivité de l’interface

Action :

```text
Pendant le traitement, cliquer sur : Tester la réactivité UI
```

Résultat attendu :

```text
Un Toast s’affiche immédiatement.
```

Ce test montre que le thread principal n’est pas bloqué.

---

### Test 3 — Calcul avec AsyncTask

Action :

```text
Cliquer sur : Lancer calcul avec AsyncTask
```

Résultat attendu :

```text
La progression avance de 0 % à 100 %.
```

À la fin, l’application affiche :

```text
État : calcul terminé. Résultat = ...
```

Ce test montre que le calcul lourd est exécuté dans un thread de fond.

---

## Résultat obtenu

L’application fonctionne correctement :

* le bouton Thread lance une tâche sans bloquer l’interface ;
* l’image reste affichée dans la zone d’aperçu ;
* le pourcentage atteint 100 % à la fin du traitement ;
* le bouton Toast reste fonctionnel même pendant l’exécution ;
* le calcul AsyncTask se termine avec un résultat affiché.

---

## Technologies utilisées

* Android Studio
* Java
* XML
* Thread
* Handler
* Looper
* AsyncTask
* MaterialButton
* CardView

---

## Remarque

`AsyncTask` est aujourd’hui considérée comme une ancienne approche dans Android moderne.
Cependant, elle reste utile dans ce lab pour comprendre les notions de base liées aux threads, au traitement en arrière-plan et à la mise à jour de l’interface graphique.

---









## Conclusion

Ce lab m’a permis de comprendre que les traitements longs ne doivent pas être exécutés directement dans le **UI Thread**.
L’utilisation d’un `Thread` permet de lancer une tâche en arrière-plan, tandis que `Handler` permet de revenir proprement vers le thread principal pour modifier l’interface.

L’utilisation de `AsyncTask` m’a aussi permis de comprendre le principe d’un traitement progressif avec une `ProgressBar`.

Ce projet montre donc comment garder une application Android fluide et réactive pendant l’exécution de tâches longues.
