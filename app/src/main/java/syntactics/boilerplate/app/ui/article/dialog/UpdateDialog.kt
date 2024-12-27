package syntactics.boilerplate.app.ui.article.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import syntactics.boilerplate.app.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import syntactics.android.app.R
import syntactics.android.app.databinding.DialogUpdateBinding

@AndroidEntryPoint
class UpdateDialog : BottomSheetDialogFragment(){

    private var viewBinding: DialogUpdateBinding? = null
    private var callback: ProfileSaveDialogCallBack? = null
    private var tempTitle: String?=null
    private var descTitle: String?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_update,
            container,
            false
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = DialogUpdateBinding.bind(view)
        setClickListener()
        setView()
    }

    private fun setView() = viewBinding?.run {

    }

    private fun setClickListener() {
        viewBinding?.titleEditText?.setText(tempTitle)
        viewBinding?.descriptionEditText?.setText(descTitle)

        viewBinding?.saveButton?.setOnSingleClickListener {
            callback?.onSuccess(this, title =viewBinding?.titleEditText?.text.toString(), desc = viewBinding?.descriptionEditText?.text.toString())
            dismiss()
        }

        viewBinding?.deleteButton?.setOnSingleClickListener {
           callback?.onMyAccountClicked(this)
            dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    interface ProfileSaveDialogCallBack {
        fun onMyAccountClicked(dialog: UpdateDialog)
        fun onSuccess(dialog: UpdateDialog,title:String, desc: String)
    }


    companion object {
        fun newInstance(callback: ProfileSaveDialogCallBack? = null,title:String, desc: String) = UpdateDialog()
            .apply {
                this.callback = callback
                this.tempTitle = title
                this.descTitle = desc
            }

        val TAG: String = UpdateDialog::class.java.simpleName
    }
}