package likelion.project.dongnation.ui.main

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.ActivityMainBinding
import likelion.project.dongnation.ui.home.HomeFragment
import likelion.project.dongnation.ui.login.LoginFragment
import likelion.project.dongnation.ui.map.MapFragment
import likelion.project.dongnation.ui.onboarding.OnboardFragment
import likelion.project.dongnation.ui.userInfo.UserInfoFragment

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    val permissionList = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private var isFirstVisitor = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
        setContentView(activityMainBinding.root)
        navigateToPermissionOrOnboardingOrLogin()
        observe()
    }

    fun replaceFragment(name: String, addToBackStack: Boolean, bundle: Bundle?) {
        // Fragment 교체 상태로 설정
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        var newFragment: Fragment? = null
        var oldFragment: Fragment? = null

        if (newFragment != null) {
            oldFragment = newFragment
        }

        // 새로운 Fragment를 담을 변수
        newFragment = when (name) {
            LOGIN_FRAGMENT -> LoginFragment()
            USER_INFO_FRAGMENT -> UserInfoFragment()
            HOME_FRAGMENT -> HomeFragment()
            MAP_FRAGMENT -> MapFragment()
            ONBOARDING_FRAGMENT -> OnboardFragment()
            else -> Fragment()
        }

        newFragment?.arguments = bundle

        if (newFragment != null) {
            if (oldFragment != null) {
                oldFragment?.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                oldFragment?.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                oldFragment?.enterTransition = null
                oldFragment?.returnTransition = null
            }

            newFragment?.exitTransition = null
            newFragment?.reenterTransition = null
            newFragment?.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            newFragment?.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

            // Fragment를 교채한다.
            fragmentTransaction.replace(R.id.fragmentContainerView_main, newFragment!!)
            if (addToBackStack == true) {
                // Fragment를 Backstack에 넣어 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령이 동작하도록 한다.
            fragmentTransaction.commit()
        }
    }


    private fun observe() {
        lifecycleScope.launch {
            viewModel.isFistVisitor.collect {
                isFirstVisitor = it
            }
        }
    }

    private fun navigateToPermissionOrOnboardingOrLogin() {
        CoroutineScope(Dispatchers.Main).launch {
            if ((checkPermission() || shouldShowPermissionRationale()) && isFirstVisitor) {
                replaceFragment(ONBOARDING_FRAGMENT, false, null)
            } else if (!isFirstVisitor) {
                replaceFragment(LOGIN_FRAGMENT, false, null)
            } else {
                replaceFragment(PERMISSION_FRAGMENT, false, null)
            }
            delay(500)
        }
    }

    fun shouldShowPermissionRationale(): Boolean {
        return permissionList.any { permission ->
            shouldShowRequestPermissionRationale(permission)
        }
    }

    fun checkPermission(): Boolean {
        return permissionList.all { permission ->
            ActivityCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        val LOGIN_FRAGMENT = "LoginFragment"
        val USER_INFO_FRAGMENT = "UserInfoFragment"
        val HOME_FRAGMENT = "HomeFragment"
        val MAP_FRAGMENT = "MapFragment"
        val ONBOARDING_FRAGMENT = "OnboardingFragment"
        val PERMISSION_FRAGMENT = "PermissionFragment"
        const val PERMISSION_REQUEST_ACCESS = 100
    }
}