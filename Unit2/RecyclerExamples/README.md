# Unit 2: RecyclerView

This project demonstrates various implementations of `RecyclerView` as covered in the [CodePath guide](https://guides.codepath.com/android/using-the-recyclerview). The examples highlight key components and features of `RecyclerView`, with additional comments and clarifications based on the lesson.

## Project Overview

The project includes **three separate activities** to showcase different versions of `RecyclerView`:

1. **Basic RecyclerView**: Demonstrates the initial setup and implementation of a `RecyclerView`.
2. **RecyclerView with ListAdapter**: Implements a more advanced version using `ListAdapter` to manage list changes more efficiently.
3. **Advanced RecyclerView**: Adds more functionality, such as endless scrolling, animations, and touch event handling.

## Key Features

- **Detailed Comments**: Includes comments on parts of the code that were not entirely clear in the lesson or article, providing additional context and explanation.
- **Multiple Activity Demos**: Each activity showcases a different implementation of `RecyclerView`:
    - **Example 1**: Basic `RecyclerView`.
    - **Example 2**: `RecyclerView` with `ListAdapter`.
    - **Example 3**: Advanced `RecyclerView` with enhanced features.
- **Navigation Between Examples**: Each activity displays the example number and title at the top and includes buttons to jump to other activity examples for easy navigation.

## Activities

1. **UserListActivity** (Example 1):
    - Demonstrates the basic usage of `RecyclerView` with simple data binding and layout management.

2. **UserListActivityListAdapter** (Example 2):
    - Implements `RecyclerView` using a `ListAdapter` to efficiently handle list changes with `DiffUtil`.

3. **UserListAdvancedActivity** (Example 3):
    - Showcases advanced features like:
        - Endless Scrolling using a custom `EndlessRecyclerViewScrollListener`.
        - Item Animations with `SlideInUpAnimator`.
        - Touch Event Handling to display contact information using `Toast`.
        - Pull-to-Refresh functionality using `SwipeRefreshLayout`.

## How to Use

- **Run the Project**:  
  Open the project in Android Studio, connect an Android device or use an emulator, and run the project to see each example in action.

- **Switch Between Activities**:  
  Use the buttons at the bottom of each activity to navigate between Example 1, Example 2, and Example 3.

## Additional Resources

- [Using the RecyclerView - CodePath Guide](https://guides.codepath.com/android/using-the-recyclerview)  
  A comprehensive guide to understanding and implementing `RecyclerView` in Android.
