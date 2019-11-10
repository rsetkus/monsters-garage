package lt.setkus.monstersgarage

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import timber.log.Timber
import java.lang.IllegalStateException

const val GARAGE_MODULE = "storage"
const val PROVIDER_CLASS = "lt.setkus.monstersgarage.MonstersGarageFeature"

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val splitInstallManager = SplitInstallManagerFactory.create(getApplication())

    private val listener = SplitInstallStateUpdatedListener { state ->
        if (state.sessionId() == sessionId) {
            when (state.status()) {
                SplitInstallSessionStatus.FAILED -> {
                    Timber.d("Module install failed with ${state.errorCode()}")
                    Toast.makeText(getApplication(), "Module install failed with ${state.errorCode()}", Toast.LENGTH_SHORT).show()
                }
                SplitInstallSessionStatus.INSTALLED -> {
                    Toast.makeText(getApplication(), "Storage module installed successfully", Toast.LENGTH_SHORT).show()
                }
                else -> Timber.d("Status: ${state.status()}")
            }
        }
    }

    private var sessionId = 0
    private var garageFeature: GarageFeature? = null

    init {
        splitInstallManager.registerListener(listener)
    }

    override fun onCleared() {
        splitInstallManager.unregisterListener(listener)
        super.onCleared()
    }

    fun enterGarage(): String {
        if (garageFeature == null) {
            if (isStorageInstalled()) {
                initializeGarageModule()
            } else {
                requestStorageInstall()
            }
        }

        return garageFeature?.getMonsterName() ?: throw IllegalStateException("Failed to initialize garage module")
    }

    private fun initializeGarageModule() {
        garageFeature = Class.forName(PROVIDER_CLASS).kotlin.objectInstance as GarageFeature
    }

    private fun isStorageInstalled() =
        if (BuildConfig.DEBUG) true else splitInstallManager.installedModules.contains(GARAGE_MODULE)

    private fun requestStorageInstall() {
        Toast.makeText(getApplication(), "Requesting storage module install", Toast.LENGTH_SHORT).show()
        val request =
            SplitInstallRequest
                .newBuilder()
                .addModule("storage")
                .build()

        splitInstallManager
            .startInstall(request)
            .addOnSuccessListener { id -> sessionId = id }
            .addOnFailureListener { exception ->
                Timber.e(exception, "Error installing module:")
                Toast.makeText(getApplication(), "Error requesting module install", Toast.LENGTH_SHORT).show()
            }
    }
}