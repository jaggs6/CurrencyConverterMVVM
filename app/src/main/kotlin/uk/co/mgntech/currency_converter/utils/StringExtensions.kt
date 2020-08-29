package uk.co.mgntech.currency_converter.utils

import android.text.Editable

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
