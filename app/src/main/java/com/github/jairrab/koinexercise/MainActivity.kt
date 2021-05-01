package com.github.jairrab.koinexercise

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.github.jairrab.koinexercise.databinding.ActivityMainBinding
import com.github.jairrab.koinexercise.databinding.Module1FragmentABinding
import com.github.jairrab.koinexercise.databinding.Module1FragmentBBinding
import com.github.jairrab.koinexercise.databinding.Module1FragmentCBinding
import com.github.jairrab.viewbindingutility.viewBinding
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.scope.activityScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.context.startKoin
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope
import org.koin.dsl.module

//region APPLICATION
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(Modules.appModule)
        }
    }
}
//endregion

//region MAIN ACTIVITY
class MainActivity : AppCompatActivity(), KoinScopeComponent {
    private val binding by viewBinding { ActivityMainBinding.inflate(it) }
    override val scope: Scope by lazy { activityScope() }

    // Lazy injected MySimplePresenter
    private val presenter: SimplePresenter get() = scope.get()

    //Lazy inject ViewModel
    private val viewModel by viewModel<ActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.helloLd.observe(this, Observer {
            binding.text2.text = it
        })

        binding.text3.isVisible = true
        binding.text3.text = presenter.sayHello()

        binding.button.setOnClickListener {
            viewModel.testProcessDeath()
        }
    }
}

class ActivityViewModel(
    private val repo: HelloRepository,
) : BaseViewModel() {
    val helloLd = MutableLiveData<String>()

    init {
        Log.v("koin_test", "Initializing $this")
    }

    fun testProcessDeath() {
        val giveHello = repo.giveHello()
        helloLd.value = "NON-SAVED STATE\n$giveHello"
    }

    override fun onCleared() {
        super.onCleared()
        Log.v("koin_test", "Cleared $this")
    }
}

interface SimplePresenter {
    fun sayHello(): String
}

class MySimplePresenter() : SimplePresenter {
    override fun sayHello() = "Hello from\n$this"
}
//endregion

//region FRAGMENT A
@KoinApiExtension
class Module1FragmentA : BaseFragment(R.layout.module_1_fragment_a) {
    private val binding by viewBinding { Module1FragmentABinding.bind(it) }

    private val viewModel by viewModel<Module1FragmentAViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.helloLd.observe(viewLifecycleOwner, Observer {
            binding.text2.text = it
        })

        binding.button1.setOnClickListener {
            viewModel.testProcessDeath()
        }

        binding.button2.setOnClickListener {
            val value = "SavedStateHandle initialized w/ bundle passed from Fragment A"
            val bundle = bundleOf("frag_b_args" to value)
            navigate(R.id.action_module_1_fragment_a_to_module_1_fragment_b, bundle)
        }
    }
}

class Module1FragmentAViewModel(
    private val repo: HelloRepository,
) : BaseViewModel() {
    val helloLd = MutableLiveData<String>()

    init {
        Log.v("koin_test", "Initializing $this")
    }

    fun testProcessDeath() {
        val giveHello = repo.giveHello()
        helloLd.value = "NON-SAVED STATE\n$giveHello"
    }

    override fun onCleared() {
        super.onCleared()
        Log.v("koin_test", "Cleared $this")
    }
}
//endregion

//region FRAGMENT B
class Module1FragmentB : BaseFragment(R.layout.module_1_fragment_b) {
    private val binding by viewBinding { Module1FragmentBBinding.bind(it) }

    private val viewModel by viewModel<Module1FragmentBViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.helloLd.observe(viewLifecycleOwner, Observer {
            binding.text2.text = it
        })

        binding.button1.setOnClickListener {
            viewModel.testProcessDeath()
        }

        binding.button2.setOnClickListener {
            navigate(
                Module1FragmentBDirections.actionModule1FragmentBToModule1FragmentC(
                    data1 = "SavedStateHandle initialized w/ Fragment B bundle"
                )
            )
        }

        binding.button3.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

class Module1FragmentBViewModel(
    private val repo: HelloRepository,
) : BaseViewModel() {
    val helloLd = MutableLiveData<String>()

    init {
        Log.v("koin_test", "Initializing $this")
    }

    fun testProcessDeath() {
        val giveHello = repo.giveHello()
        helloLd.value = "NON-SAVED STATE\n$giveHello"
    }

    override fun onCleared() {
        super.onCleared()
        Log.v("koin_test", "Cleared $this")
    }
}
//endregion

//region FRAGMENT C
class Module1FragmentC : BaseFragment(R.layout.module_1_fragment_c) {
    private val binding by viewBinding { Module1FragmentCBinding.bind(it) }

    private val viewModel by viewModel<Module1FragmentCViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().activityScope()

        viewModel.helloLd.observe(viewLifecycleOwner, Observer {
            binding.text2.text = it
        })

        binding.button1.setOnClickListener {
            viewModel.testProcessDeath()
        }

        binding.button2.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

class Module1FragmentCViewModel(
    private val repo: HelloRepository,
) : BaseViewModel() {
    val helloLd = MutableLiveData<String>()

    init {
        Log.v("koin_test", "Initializing $this")
    }

    fun testProcessDeath() {
        val giveHello = repo.giveHello()
        helloLd.value = "NON-SAVED STATE\n$giveHello"
    }

    override fun onCleared() {
        super.onCleared()
        Log.v("koin_test", "Cleared $this")
    }
}
//endregion

//region REPOSITORY
interface HelloRepository {
    fun giveHello(): String
}

class HelloRepositoryImpl() : HelloRepository {
    override fun giveHello() = "Hello Koin from $this"
}
//endregion

//region KOIN MODULES
object Modules {
    val appModule = module {
        //factory - to produce a new instance each time the by inject() or get() is called
        //factory { MySimplePresenter(get()) }

        //scope - to produce an instance tied to a scope
        scope<MainActivity> {
            scoped<SimplePresenter> {
                MySimplePresenter()
            }
        }

        //factory { MySimplePresenter() }

        // single instance of HelloRepository
        single<HelloRepository> { HelloRepositoryImpl() }

        viewModel { ActivityViewModel(get()) }

        viewModel { Module1FragmentAViewModel(get()) }

        viewModel { Module1FragmentBViewModel(get()) }

        viewModel { Module1FragmentCViewModel(get()) }
    }
}
//endregion