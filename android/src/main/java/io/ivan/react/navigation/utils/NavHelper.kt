package io.ivan.react.navigation.utils

import android.content.Context
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import io.ivan.react.navigation.R
import io.ivan.react.navigation.view.ARG_COMPONENT_NAME
import io.ivan.react.navigation.view.ARG_LAUNCH_OPTIONS
import io.ivan.react.navigation.view.RNFragment

fun createNavHostFragmentWithoutGraph() = NavHostFragment.create(0)

fun NavController.setStartDestination(startDestination: NavDestination?) {
    startDestination ?: return

    graph = NavGraphNavigator(navigatorProvider).createDestination().also {
        it.addDestination(startDestination)
        it.startDestination = startDestination.id
    }
}

fun buildDestination(context: Context, fm: FragmentManager, destinationName: String): NavDestination {
    return FragmentNavigator(context, fm, R.id.content).createDestination().also {
        val viewId = ViewCompat.generateViewId()
        it.id = viewId
        it.className = RNFragment::class.java.name
        it.addArgument(ARG_COMPONENT_NAME, NavArgumentBuilder().let { arg ->
            arg.defaultValue = destinationName
            arg.build()
        })
        it.addArgument(ARG_LAUNCH_OPTIONS, NavArgumentBuilder().let { arg ->
            arg.defaultValue = Bundle().also { bundle -> bundle.putString("screenID", viewId.toString()) }
            arg.build()
        })
    }
}
