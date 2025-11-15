package com.example.gopayurself.navigation

sealed class Screen {
    object Login : Screen()
    object Dashboard : Screen()
    object CreateGroup : Screen()
    object GroupDetail : Screen()
}

class NavigationManager {
    private val navigationStack = mutableListOf<Screen>(Screen.Login)

    fun navigate(screen: Screen) {
        navigationStack.add(screen)
    }

    fun navigateBack(): Boolean {
        return if (navigationStack.size > 1) {
            navigationStack.removeAt(navigationStack.size - 1)
            true
        } else {
            false
        }
    }

    fun getCurrentScreen(): Screen {
        return navigationStack.last()
    }

    fun clearAndNavigate(screen: Screen) {
        navigationStack.clear()
        navigationStack.add(screen)
    }
}