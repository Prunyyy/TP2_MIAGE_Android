package ci.miage.mob.networkls

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View

class PlanAppartement(context: Context) : View(context) {

    private var backgroundImage: Bitmap? = null

    fun setBackground(bitmap: Bitmap) {
        this.backgroundImage = bitmap
        invalidate() // Demande à la vue de se redessiner
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        backgroundImage?.let {
            // Dessiner l'image de fond à l'échelle de la vue
            val srcRect = Rect(0, 0, it.width, it.height)
            val destRect = Rect(0, 0, width, height)
            canvas.drawBitmap(it, srcRect, destRect, null)
        }
    }
}
