package io.ivan.react.navigation.view

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.PixelUtil
import io.ivan.react.navigation.utils.ACTION_DISPATCH_SWITCH_TAB
import io.ivan.react.navigation.utils.Store
import io.ivan.react.navigation.utils.optInt
import io.ivan.react.navigation.view.model.RootViewModel
import io.ivan.react.navigation.view.widget.SwipeControllableViewPager
import java.util.*

class RNTabBarFragment : Fragment() {

    private lateinit var view: ViewGroup
    private lateinit var viewPager: SwipeControllableViewPager

    private val viewModel: RootViewModel by lazy { ViewModelProvider(requireActivity()).get(RootViewModel::class.java) }
    private val tabBarContainerId by lazy { View.generateViewId() }

    private val tabBarHeight = PixelUtil.toPixelFromDIP(56f).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.beginTransaction()
            .add(tabBarContainerId, createTabBarFragment())
            .commitNowAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::view.isInitialized) {
            view = with(inflater.context) {
                FrameLayout(this).also {
                    it.addView(createTabBarContainer(this))
                    it.addView(createContentContainer(this))
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.tabs?.let { viewPager.adapter = RNTabPageAdapter(childFragmentManager, it) }

        Store.reducer(ACTION_DISPATCH_SWITCH_TAB)?.observe(requireActivity(), Observer { state ->
            val data = state as ReadableMap
            val index = data.optInt("index")
            viewPager.setCurrentItem(index, false)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager.clearOnPageChangeListeners()
    }

    private fun createTabBarFragment(): RNFragment =
        RNFragment().apply {
            mainComponentName = viewModel.tabBarComponentName
            launchOptions = Bundle().also {
                it.putSerializable("tabs", pageOptionList())
            }
        }

    private fun createTabBarContainer(context: Context) =
        FrameLayout(context).apply {
            id = tabBarContainerId
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                tabBarHeight,
                Gravity.BOTTOM
            )
        }

    private fun createContentContainer(context: Context) =
        SwipeControllableViewPager(context).apply {
            viewPager = this
            id = View.generateViewId()
            isEnabled = false
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(0, 0, 0, tabBarHeight)
            }
        }

    private fun pageOptionList(): ArrayList<Bundle?> =
        (viewModel.tabs?.pages?.map { Arguments.toBundle(it.options) } ?: mutableListOf()) as ArrayList

}
