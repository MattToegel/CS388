= Extra Credit Challenge: Trivia Game App with Firebase Firestore Leaderboard

**Objective:** Create a trivia game where users answer random questions, track their high scores, and compete on a public leaderboard stored in Firebase Firestore.

== Core Goals

* **Fetch Trivia Questions (2 pts)**
   * Use a public trivia API, such as the [Open Trivia Database](https://opentdb.com/api_config.php), to fetch trivia questions.
   * Parse the JSON response to display a set of 5 random questions in the app.
   * ![Image/GIF showing API response](http://i.imgur.com/link/to/your/gif/file.gif)

* **Display Trivia Questions and Capture Answers (2 pts)**
   * Design an Activity to display trivia questions with multiple-choice answers.
   * Allow users to select their answer and navigate to the next question until all 5 questions are answered.
   * ![Image/GIF showing question layout](http://i.imgur.com/link/to/your/gif/file.gif)

* **Show Quiz Results (2 pts)**
   * After completing the quiz, display a results screen showing the number of correct answers and a motivational message based on performance.
   * ![Image/GIF showing results screen](http://i.imgur.com/link/to/your/gif/file.gif)

* **Save High Score in Firebase Firestore (2 pts)**
   * Record the user’s highest score and store it in Firestore under a "leaderboard" collection.
   * Each Firestore document should contain:
     * `userId` or nickname
     * `score`
     * `timestamp` of quiz completion
   * ![Image/GIF showing Firestore save](http://i.imgur.com/link/to/your/gif/file.gif)

* **Display a Global Leaderboard (2 pts)**
   * Create an Activity that queries Firestore to display the top 10 scores globally, sorted by score and timestamp.
   * Allow users to view how they rank against others.
   * ![Image/GIF showing leaderboard](http://i.imgur.com/link/to/your/gif/file.gif)

== Stretch Goals

The following **stretch** functionality can be implemented for additional credit:

* **User Profile (+1 pts)**
   * Allow users to enter a nickname before starting the quiz to personalize the leaderboard entries.
   * Save the nickname along with their scores in Firestore.

* **Animated Transitions (+1 pts)**
   * Add animations between question screens for a smoother user experience.
   * Include a custom animation when displaying the results screen.

* **Offline Mode (+3 pts)**
   * Cache trivia questions locally using Room or SharedPreferences, allowing the quiz to be playable offline.
   * Sync scores to Firestore when an internet connection is available.

== Implementation Guide

=== Step 1: Setup Firebase Firestore

1. Create a new project in Firebase Console or add Firestore to an existing project.
2. Enable Firestore and set up a "leaderboard" collection to store user scores.
3. Configure Firestore security rules to allow read/write access for authenticated users only.

=== Step 2: Build the Trivia Quiz Interface

1. Fetch trivia questions from the Open Trivia Database or another trivia API.
2. Display questions with multiple-choice answers.
3. Collect user responses and navigate through questions until the quiz is complete.

=== Step 3: Store High Scores in Firestore

1. At the end of the quiz, save the user’s score to Firestore under the "leaderboard" collection.
2. Each entry should include the user’s nickname, score, and a timestamp.

=== Step 4: Display the Leaderboard

1. Query Firestore to retrieve the top 10 highest scores, sorted by score and timestamp.
2. Display these results in a RecyclerView in the app.

=== Step 5: Add UI Enhancements and Stretch Features

* Implement animations between question screens and the results screen.
* Add a nickname entry prompt at the start of the quiz and store this information with scores in Firestore.
* Allow users to play offline by caching trivia questions and syncing scores when online.

== Example Firestore Rules

[source,plaintext]
----
service cloud.firestore {
  match /databases/{database}/documents {
    match /leaderboard/{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
----

== Project Resources

* **Public Trivia API**: [Open Trivia Database](https://opentdb.com/api_config.php)
* **Firebase Documentation**: https://firebase.google.com/docs
* **RecyclerView Setup**: https://developer.android.com/guide/topics/ui/layout/recyclerview
* **Firestore Security Rules**: https://firebase.google.com/docs/firestore/security/get-started

== Notes

Replace this section with any challenges you encountered while building the app.

== License

```plaintext
    Copyright [yyyy] [Your Name]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
