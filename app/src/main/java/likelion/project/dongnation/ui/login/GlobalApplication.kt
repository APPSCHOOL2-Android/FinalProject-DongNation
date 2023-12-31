package likelion.project.dongnation.ui.login

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import likelion.project.dongnation.BuildConfig
import java.util.Properties

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        KakaoSdk.init(this, "${BuildConfig.KAKAO_NATIVE_APP_KEY}")

        // Naver SDK 초기화
        NaverIdLoginSDK.initialize(this, "${BuildConfig.NAVER_OAUTH_CLIENT_ID}", "${BuildConfig.NAVER_OAUTH_CLIENT_SECRET}", "${BuildConfig.NAVER_OAUTH_CLIENT_NAME}")
    }
}