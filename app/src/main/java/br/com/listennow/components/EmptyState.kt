package br.com.listennow.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import br.com.listennow.R
import br.com.listennow.databinding.EmptyStateBinding

class EmptyState : ConstraintLayout {
    var binding: EmptyStateBinding =
        EmptyStateBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context): this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?): this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int): super(context, attributeSet, defStyleAttr) {
        context.obtainStyledAttributes(attributeSet, R.styleable.EmptyState).let { attrs ->
            binding.emptyStateText.text = attrs.getText(R.styleable.EmptyState_message)
            binding.emptyStateActionButton.setIconResource(attrs.getResourceId(R.styleable.EmptyState_buttonIcon, R.drawable.ic_search))

            attrs.getText(R.styleable.EmptyState_buttonText)?.let {
                binding.emptyStateActionButton.text = it
            }
        }
    }

    fun hideAction() {
        binding.emptyStateActionButton.visibility = View.GONE
    }
}