package ge.baqar.gogia.goefolk.ui.account.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ge.baqar.gogia.goefolk.databinding.FragmentAccountRegisterBinding

class RegisterFragment : Fragment() {
    private var binding: FragmentAccountRegisterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountRegisterBinding.inflate(inflater, container, false)

        return binding?.root!!
    }
}