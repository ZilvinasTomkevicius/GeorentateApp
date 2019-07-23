package app.georentate.georentate_app.ux

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat.startActivity

class EnableLocationListener(context: Context): View.OnClickListener {

    private var mContext: Context = context

    override fun onClick(p0: View?) {
        startActivity(mContext, Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), null)
    }
}