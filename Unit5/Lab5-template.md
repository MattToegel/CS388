# Lab 5: ArticleSearch Pt 2

Course Link: [CodePath Android Course](https://courses.codepath.org/courses/and102/unit/5#!labs)

Submitted by: **Your Name Here** <!-- Replace 'Your Name Here' with your actual name -->

**NYT Article Search Pt 2** is an app designed to maintain functionality while offline by caching the latest data fetched from the NYT API, ensuring a smooth user experience even without network connectivity.

Time spent: **X** hours spent in total <!-- Replace 'X' with the number of hours you spent on this project -->

## Application Features

### Required Features

The following **required** functionality is completed:

- [ ] (2 pts) **Most recently fetched data is stored locally in a database**
  - The app should cache the latest articles fetched from the NYT API in a local SQLite database using Room.
  - If the user has fetched data recently, those articles should be available offline.
  - Ensure old cached data is properly replaced with new data upon successful network fetches.
  - ![GIF showing database caching functionality](http://i.imgur.com/link/to/your/gif/file.gif) <!-- Replace this link with your actual image/GIF link -->

- [ ] (2 pts) **If user turns on airplane mode and closes and reopens app, old data from the database should be loaded**
  - ![GIF showing offline mode functionality](http://i.imgur.com/link/to/your/gif/file.gif) <!-- Replace this link with your actual image/GIF link -->

### Stretch Features

The following **stretch** functionality is implemented:

- [ ] (2 pts) **Add Swipe To Refresh to force a new network call to get new data**
  - ![GIF showing Swipe to Refresh](http://i.imgur.com/link/to/your/gif/file.gif) <!-- Replace this link with your actual image/GIF link -->

- [ ] (2 pts) **Add setting toggle for user to create preference for caching data or not (Using Shared Preferences)**
  - ![GIF showing Shared Preferences for caching](http://i.imgur.com/link/to/your/gif/file.gif) <!-- Replace this link with your actual image/GIF link -->

- [ ] (+3 pts) **Implement a Search UI to filter current RecyclerView entries or fetch data from the search API with query**
  - ![GIF showing Search UI functionality](http://i.imgur.com/link/to/your/gif/file.gif) <!-- Replace this link with your actual image/GIF link -->

- [ ] (2 pts) **Listen to network connectivity changes and create a UI to let people know they are offline and automatically reload new data if connectivity returns**
  - ![GIF showing network connectivity detection](http://i.imgur.com/link/to/your/gif/file.gif) <!-- Replace this link with your actual image/GIF link -->

## Notes

Describe any challenges encountered while building the app. <!-- Replace this with your specific challenges and experiences -->

## Resources

- [Data storage with Room](https://developer.android.com/training/data-storage/room)
- [Swipe To Refresh](https://developer.android.com/training/swipe/add-swipe-interface)
- [Save key-value data with Shared Preferences](https://developer.android.com/training/data-storage/shared-preferences)
- [Android Search View](https://developer.android.com/reference/android/widget/SearchView)
- [Monitor connectivity status and connection metering](https://developer.android.com/training/monitoring-device-state/connectivity-status-type)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

## License

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
